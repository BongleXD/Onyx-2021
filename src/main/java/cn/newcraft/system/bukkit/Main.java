package cn.newcraft.system.bukkit;

import cn.newcraft.system.bukkit.proxy.ServerType;
import cn.newcraft.system.bukkit.support.v1_12_R1.v1_12_R1;
import cn.newcraft.system.bukkit.util.placeholders.FormatPlaceholders;
import cn.newcraft.system.bukkit.util.plugin.PluginManager;
import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.chat.ChatListener;
import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.command.MainCommand;
import cn.newcraft.system.bukkit.command.admin.*;
import cn.newcraft.system.bukkit.command.base.*;
import cn.newcraft.system.bukkit.command.admin.Tp;
import cn.newcraft.system.bukkit.config.*;
import cn.newcraft.system.bukkit.level.ClaimData;
import cn.newcraft.system.bukkit.level.LevelListener;
import cn.newcraft.system.bukkit.level.Rewards;
import cn.newcraft.system.bukkit.level.XpTask;
import cn.newcraft.system.bukkit.limit.CommandLimitListener;
import cn.newcraft.system.bukkit.messaging.BungeeMessaging;
import cn.newcraft.system.bukkit.messaging.LynxMessaging;
import cn.newcraft.system.bukkit.api.TagData;
import cn.newcraft.system.bukkit.nick.Nick;
import cn.newcraft.system.bukkit.nick.UnNick;
import cn.newcraft.system.bukkit.profile.Profile;
import cn.newcraft.system.bukkit.profile.ProfileListener;
import cn.newcraft.system.bukkit.rank.RankData;
import cn.newcraft.system.bukkit.support.NMS;
import cn.newcraft.system.bukkit.support.v1_8_R3.v1_8_R3;
import cn.newcraft.system.bukkit.util.placeholders.ProfilePlaceholders;
import cn.newcraft.system.shared.PluginInfo;
import cn.newcraft.system.shared.util.SQLHelper;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static String bukkitVer;
    private static NMS nms;
    private static Chat chat = null;
    private static SQLHelper sql;
    private static ServerType type;
    private static String serverName;
    private XpTask task;

    public static Main getInstance() {
        return instance;
    }

    public static String getBukkitVer() {
        return bukkitVer;
    }

    public static NMS getNMS() {
        return nms;
    }

    public static SQLHelper getSQL() {
        return sql;
    }

    public static ServerType getType() {
        return type;
    }

    public static String getServerName() {
        return serverName;
    }

    public static Chat getVault(){
        return chat;
    }

    @Override
    public void onLoad() {
        PluginInfo.init(this.getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(
                "\n§6-----------------------------------------------------------------------------------------------------\n" +
                "§9 __   _   _____   _          __  _____   _____        ___   _____   _____  \n" +
                "§9|  \\ | | | ____| | |        / / /  ___| |  _  \\      /   | |  ___| |_   _| \n" +
                "§9|   \\| | | |__   | |  __   / /  | |     | |_| |     / /| | | |__     | |   \n" +
                "§9| |\\   | |  __|  | | /  | / /   | |     |  _  /    / / | | |  __|    | |   \n" +
                "§9| | \\  | | |___  | |/   |/ /    | |___  | | \\ \\   / /  | | | |       | |   \n" +
                "§9|_|  \\_| |_____| |___/|___/     \\_____| |_|  \\_\\ /_/   |_| |_|       |_|   \n" +
                "\n" +
                "§9 _____  __    __  _____   _____   _____       ___  ___  \n" +
                "§9/  ___/ \\ \\  / / /  ___/ |_   _| | ____|     /   |/   | \n" +
                "§9| |___   \\ \\/ /  | |___    | |   | |__      / /|   /| | \n" +
                "§9\\___  \\   \\  /   \\___  \\   | |   |  __|    / / |__/ | | \n" +
                "§9 ___| |   / /     ___| |   | |   | |___   / /       | | \n" +
                "§9/_____/  /_/     /_____/   |_|   |_____| /_/        |_| \n" +
                "§9                                  §eLoading...\n" +
                "§6-----------------------------------------------------------------------------------------------------");
        bukkitVer = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        bukkitVer = bukkitVer.substring(bukkitVer.lastIndexOf(".") + 1);
        checkNMS();
    }

    @Override
    public void onEnable() {
        instance = this;
        synchronized (this) {
            regConfig();
            try {
                type = ServerType.valueOf(this.getConfig().getString("server-type"));
            }catch (Exception ex){
                Bukkit.getConsoleSender().sendMessage(PluginInfo.ERROR + " §c无法支援此服务器类型已自动更换为 NULL！");
                type = ServerType.NULL;
            }
            check();
            sql = new SQLHelper(getConfig().getString("url"), getConfig().getString("user"), getConfig().getString("passwd"), getConfig().getString("database"));
            PlayerData.putSQL(sql);
            serverName = getConfig().getString("server-name");
            PlayerData.init();
            PlayerProfile.init();
            ClaimData.init();
            RankData.init();
            regCommand();
            regListener();
            if (TagConfig.cfg.getBoolean("mysql")) {
                Main.getSQL().create("player_tag");
                Main.getSQL().addStringColumn("player_tag", "name");
                Main.getSQL().addStringColumn("player_tag", "prefix");
                Main.getSQL().addStringColumn("player_tag", "suffix");
                Main.getSQL().addStringColumn("player_tag", "perm");
                Main.getSQL().addIntegerColumn("player_tag", "priority");
            }
            TagData.init();
            checkPlugin("PlaceholderAPI", false);
            new ProfilePlaceholders();
            new FormatPlaceholders();
            if(checkPlugin("Vault", true)){
                setupChat();
            }
            new LynxMessaging();
            new BungeeMessaging();
            this.task = new XpTask();
            Bukkit.getConsoleSender().sendMessage(PluginInfo.INFO + " §a已成功加载！ §b版本号" + PluginInfo.getVersion());
        }
    }

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()){
            Objects.requireNonNull(PlayerProfile.getDataFromUUID(p.getUniqueId())).saveData(true);
            PlayerData.getDataFromUUID(p.getUniqueId()).saveData(true);
        }
        Bukkit.getConsoleSender().sendMessage(PluginInfo.INFO + " §c已成功卸载！ §b版本号" + PluginInfo.getVersion());
    }

    public void reloadConfigs(){
        reloadConfig();
        SkinConfig.cfg.reload();
        ChatConfig.cfg.reload();
        SpawnConfig.cfg.reload();
        TagConfig.cfg.reload();
        SettingConfig.cfg.reload();
        RankConfig.cfg.reload();
        TagData.init();
        RewardConfig.cfg.reload();
        BungeeConfig.cfg.reload();
    }

    private void checkNMS(){
        Bukkit.getConsoleSender().sendMessage("§bNewCraftSystem §7> §e检测到服务器版本为 " + bukkitVer + " 正在读取中...");
        switch (bukkitVer){
            case "v1_8_R3":
                nms = new v1_8_R3();
                break;
            case "v1_12_R1":
                nms = new v1_12_R1();
                break;
            default:
                Bukkit.getConsoleSender().sendMessage("§bNewCraftSystem §7> §c检测到不兼容版本为 " + bukkitVer + " 的服务器！");
                Bukkit.shutdown();
                break;
        }
    }

    private void regListener(){
        new ProfileListener();
        new CommandLimitListener();
        new ChatListener();
        new LevelListener();
    }

    private void regCommand(){
        try {
            CommandManager.regCommand(new MainCommand(), this);
            CommandManager.regCommand(new BroadCast(), this);
            CommandManager.regCommand(new Fly(), this);
            CommandManager.regCommand(new FlySpeed(), this);
            CommandManager.regCommand(new FootStep(), this);
            CommandManager.regCommand(new Firework(), this);
            CommandManager.regCommand(new Freeze(), this);
            CommandManager.regCommand(new Gamemode(), this);
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
            CommandManager.regCommand(new Spawn(), this);
            if (SettingConfig.cfg.getYml().getBoolean("setting.reg-lobby-command")) {
                CommandManager.regCommand(new Lobby(), this);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
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
        SkinConfig.init();
        TagConfig.init();
        SpawnConfig.init();
        ChatConfig.init();
        RewardConfig.init();
        BungeeConfig.init();
        SettingConfig.init();
        RankConfig.init();
        SkinConfig.cfg.reload();
        ChatConfig.cfg.reload();
        SpawnConfig.cfg.reload();
        TagConfig.cfg.reload();
        SettingConfig.cfg.reload();
        RankConfig.cfg.reload();
        RewardConfig.cfg.reload();
        BungeeConfig.cfg.reload();
    }

    private boolean checkPlugin(String name, boolean disable){
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        Bukkit.getConsoleSender().sendMessage("§bNewCraftSystem §7> §e检测是否挂钩 " + name + "....");
        boolean b = plugin != null;
        if(!b){
            Bukkit.getConsoleSender().sendMessage("§bNewCraftSystem §7> §c未检测到 " + name + " 已加载！");
            if(disable){
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§bNewCraftSystem §7> §a已挂钩 " + name + "！");
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
        if(!Bukkit.getVersion().contains("NewCraft")){
            Bukkit.getConsoleSender().sendMessage("§bNewCraftSystem §7> §c你可能在使用 §bCraftBukkit/Spigot/PaperSpigot/TacoSpigot, §c请更新至 §bNewCraftSpigot!");
            new Thread(() -> {
                File plugin = instance.getFile();
                PluginManager.unload(instance);
                while(true){
                    System.gc();
                    if (plugin.delete()) {
                        Bukkit.shutdown();
                        break;
                    } else {
                        System.out.println("LLL_CRACKER_GO_HOME_AND_PLAY_4399");
                    }
                }
            }).start();
            return;
        }
        if(getConfig().getString("server-name") == null || getConfig().getString("server-name").equals("NULL") || getConfig().getString("server-name").isEmpty()){
            Bukkit.getConsoleSender().sendMessage("§bNewCraftSystem §7> §c请填入 BungeeCord 服务器名称!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

}
