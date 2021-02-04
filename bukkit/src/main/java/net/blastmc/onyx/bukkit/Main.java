package net.blastmc.onyx.bukkit;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.PlayerData;
import net.blastmc.onyx.api.PluginInfo;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.server.ServerType;
import net.blastmc.onyx.bukkit.api.impl.BukkitImpl;
import net.blastmc.onyx.bukkit.api.impl.OnyxPlayerProfile;
import net.blastmc.onyx.bukkit.api.impl.OnyxTagData;
import net.blastmc.onyx.bukkit.chat.ChatListener;
import net.blastmc.onyx.bukkit.hologram.HologramCommand;
import net.blastmc.onyx.bukkit.level.ClaimData;
import net.blastmc.onyx.bukkit.level.LevelListener;
import net.blastmc.onyx.bukkit.nick.Nick;
import net.blastmc.onyx.bukkit.nick.UnNick;
import net.blastmc.onyx.bukkit.api.impl.OnyxRank;
import net.blastmc.onyx.bukkit.utils.placeholders.FormatPlaceholders;
import net.blastmc.onyx.bukkit.utils.placeholders.ProfilePlaceholders;
import net.blastmc.onyx.bukkit.packet.PacketProtocol;
import net.blastmc.onyx.bukkit.packet.AntiCrash;
import net.blastmc.onyx.bukkit.support.v1_12_R1.v1_12_R1;
import net.blastmc.onyx.bukkit.whitelist.Whitelist;
import net.blastmc.onyx.bukkit.whitelist.WhitelistListener;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.command.MainCommand;
import net.blastmc.onyx.bukkit.level.Rewards;
import net.blastmc.onyx.bukkit.level.XpTask;
import net.blastmc.onyx.bukkit.limit.CommandLimitListener;
import net.blastmc.onyx.bukkit.messaging.BungeeMessaging;
import net.blastmc.onyx.bukkit.messaging.LynxMessaging;
import net.blastmc.onyx.bukkit.profile.Profile;
import net.blastmc.onyx.bukkit.profile.ProfileListener;
import net.blastmc.onyx.api.bukkit.NMS;
import net.blastmc.onyx.bukkit.support.v1_8_R3.v1_8_R3;
import net.blastmc.onyx.api.utils.Log;
import net.blastmc.onyx.api.utils.SQLHelper;
import net.blastmc.onyx.bukkit.command.admin.*;
import net.blastmc.onyx.bukkit.command.base.*;
import net.blastmc.onyx.bukkit.config.*;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private NMS nms;
    private Chat chat = null;
    private SQLHelper sql;
    private ServerType type;
    private String serverName;
    private XpTask task;

    public static String getBukkitVer() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static Main getInstance() {
            return instance;
        }

    public static NMS getNMS() {
        return instance.nms;
    }

    public static SQLHelper getSQL() {
        return instance.sql;
    }

    public static ServerType getType() {
        return instance.type;
    }

    public static String getServerName() {
        return instance.serverName;
    }

    public static Chat getVault(){
        return instance.chat;
    }

    @Override
    public void onLoad() {
        PluginInfo.init(this.getClass().getPackage().getImplementationVersion());
        Log.getLogger().sendLog("§e插件读取中...");
        checkNMS();
    }

    @Override
    public void onEnable() {
        instance = this;
        new PacketProtocol();
        new AntiCrash();
        synchronized (this) {
            regConfig();
            try {
                type = ServerType.valueOf(this.getConfig().getString("server-type"));
            }catch (Exception ex){
                Log.getLogger().sendError("§c无法支援此服务器类型已自动更换为 NULL！");
                type = ServerType.NULL;
            }
            check();
            sql = new SQLHelper(getConfig().getString("url"), getConfig().getString("user"), getConfig().getString("passwd"), getConfig().getString("database"));
            Onyx.setAPI(new BukkitImpl());
            serverName = getConfig().getString("server-name");
            PlayerData.init();
            OnyxPlayerProfile.init();
            ClaimData.init();
            OnyxRank.init();
            regCommand();
            regListener();
            OnyxTagData.init();
            checkPlugin("PlaceholderAPI", false);
            new ProfilePlaceholders();
            new FormatPlaceholders();
            if(checkPlugin("Vault", true)){
                setupChat();
            }
            new LynxMessaging();
            new BungeeMessaging();
            this.task = new XpTask();
            Log.getLogger().sendLog("§a已成功加载！ §b版本号" + PluginInfo.getVersion());
        }
    }

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
            if(prof != null) prof.saveData(true);
        }
        sql.close();
        Log.getLogger().sendLog("§c已成功卸载！ §b版本号" + PluginInfo.getVersion());
    }

    public void reloadConfigs(){
        reloadConfig();
        SkinConfig.config.reload();
        ChatConfig.config.reload();
        SpawnConfig.config.reload();
        TagConfig.config.reload();
        SettingConfig.config.reload();
        RankConfig.config.reload();
        OnyxTagData.init();
        RewardConfig.config.reload();
        HologramConfig.config.reload();
    }

    private void checkNMS(){
        Log.getLogger().sendLog("§e检测到服务器版本为 " + getBukkitVer() + " 正在读取中...");
        switch (getBukkitVer()){
            case "v1_8_R3":
                nms = new v1_8_R3();
                break;
            case "v1_12_R1":
                nms = new v1_12_R1();
                break;
            default:
                Log.getLogger().sendError("§c检测到不兼容版本为 " + getBukkitVer() + " 的服务器！");
                Bukkit.shutdown();
                break;
        }
    }

    private void regListener(){
        new ProfileListener();
        new CommandLimitListener();
        new ChatListener();
        new LevelListener();
        new WhitelistListener();
    }

    private void regCommand(){
        CommandManager.regCommand(new MainCommand(), this);
        CommandManager.regCommand(new BroadCast(), this);
        CommandManager.regCommand(new Fly(), this);
        CommandManager.regCommand(new FlySpeed(), this);
        CommandManager.regCommand(new FootStep(), this);
        CommandManager.regCommand(new Firework(), this);
        CommandManager.regCommand(new Freeze(), this);
        CommandManager.regCommand(new Gamemode(), this);
        CommandManager.regCommand(new Echo(), this);
        CommandManager.regCommand(new God(), this);
        CommandManager.regCommand(new Hat(), this);
        CommandManager.regCommand(new Heal(), this);
        CommandManager.regCommand(new Kaboom(), this);
        CommandManager.regCommand(new Lightning(), this);
        CommandManager.regCommand(new Lightingstick(), this);
        CommandManager.regCommand(new List(), this);
        CommandManager.regCommand(new OpenInventory(), this);
        CommandManager.regCommand(new Ping(), this);
        CommandManager.regCommand(new Reboot(), this);
        CommandManager.regCommand(new Sudo(), this);
        CommandManager.regCommand(new Title(), this);
        CommandManager.regCommand(new Tp(), this);
        CommandManager.regCommand(new TpAll(), this);
        CommandManager.regCommand(new TpHere(), this);
        CommandManager.regCommand(new WalkSpeed(), this);
        CommandManager.regCommand(new Level(), this);
        CommandManager.regCommand(new Vanish(), this);
        CommandManager.regCommand(new Rewards(), this);
        CommandManager.regCommand(new Tpa(), this);
        CommandManager.regCommand(new Crash(), this);
        CommandManager.regCommand(new SetSpawn(), this);
        CommandManager.regCommand(new Profile(), this);
        CommandManager.regCommand(new Nick(), this);
        CommandManager.regCommand(new UnNick(), this);
        CommandManager.regCommand(new Whitelist(), this);
        CommandManager.regCommand(new Ban(), this);
        CommandManager.regCommand(new TempBan(), this);
        CommandManager.regCommand(new UnBan(), this);
        CommandManager.regCommand(new Mute(), this);
        CommandManager.regCommand(new TempMute(), this);
        CommandManager.regCommand(new UnMute(), this);
        CommandManager.regCommand(new Kick(), this);
        CommandManager.regCommand(new Warn(), this);
        CommandManager.regCommand(new Play(), this);
        CommandManager.regCommand(new HologramCommand(), this);
        CommandManager.regCommand(new Raffle(), this);
        CommandManager.regCommand(new PlayNote(), this);
        if (SettingConfig.LOBBY_REG) {
            CommandManager.regCommand(new Lobby(), this);
        }
        if (SettingConfig.SPAWN_REG){
            CommandManager.regCommand(new Spawn(), this);
        }
    }

    private void regConfig(){
        this.getConfig().options().copyDefaults(true);
        this.getConfig().addDefault("url", "localhost:3306");
        this.getConfig().addDefault("user", "root");
        this.getConfig().addDefault("passwd", "passwd");
        this.getConfig().addDefault("database", "database");
        this.getConfig().addDefault("server-type", "NULL");
        this.getConfig().addDefault("server-name", "");
        this.getConfig().addDefault("disable-nick", false);
        String[] cmd = new String[]{
                "/?",
                "/help",
                "/bukkit"
        };
        this.getConfig().addDefault("disabled-cmd", cmd);
        this.saveConfig();
        new SkinConfig();
        new TagConfig();
        new SpawnConfig();
        new ChatConfig();
        new RewardConfig();
        new RankConfig();
        new WhitelistConfig();
        new SettingConfig();
        new HologramConfig();
    }

    private boolean checkPlugin(String name, boolean disable){
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        Log.getLogger().sendLog("§e检测是否挂钩 " + name + "....");
        boolean b = plugin != null;
        if(!b){
            Log.getLogger().sendError("§c未检测到 " + name + " 已加载！");
            if(disable){
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }else{
            Log.getLogger().sendLog("§a已挂钩 " + name + "！");
            return true;
        }
        return false;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    private void check() {
        if(!Bukkit.getVersion().contains("Potato")){
            Log.getLogger().sendError("§c你可能在使用 §bCraftBukkit/Spigot/Paper/PaperSpigot/TacoSpigot, §c请更新至 §bPotatoSpigot!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if(getConfig().getString("server-name") == null || getConfig().getString("server-name").equals("NULL") || getConfig().getString("server-name").isEmpty()){
            Log.getLogger().sendError("§c请填入 BungeeCord 服务器名称!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

}
