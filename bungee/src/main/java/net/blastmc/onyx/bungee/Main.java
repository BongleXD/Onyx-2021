package net.blastmc.onyx.bungee;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.PlayerData;
import net.blastmc.onyx.api.PluginInfo;
import net.blastmc.onyx.bungee.api.impl.BungeeImpl;
import net.blastmc.onyx.bungee.command.*;
import net.blastmc.onyx.bungee.config.*;
import net.blastmc.onyx.bungee.listener.DataListener;
import net.blastmc.onyx.bungee.listener.MessageListener;
import net.blastmc.onyx.bungee.listener.PunishListener;
import net.blastmc.onyx.bungee.punish.PunishManager;
import net.blastmc.onyx.bungee.task.BroadcastTask;
import net.blastmc.onyx.api.utils.Log;
import net.blastmc.onyx.api.utils.SQLHelper;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    private static Main instance;
    private SQLHelper sql;

    public static SQLHelper getSQL(){
        return instance.sql;
    }

    @Override
    public void onLoad() {
        PluginInfo.init(this.getClass().getPackage().getImplementationVersion());
        Log.getLogger().sendLog("§a读取中...");
    }

    @Override
    public void onEnable() {
        instance = this;
        regConfig();
        String url = MainConfig.cfg.getYml().getString("url");
        String user = MainConfig.cfg.getYml().getString("user");
        String passwd = MainConfig.cfg.getYml().getString("passwd");
        String database = MainConfig.cfg.getYml().getString("database");
        this.sql = new SQLHelper(url, user, passwd, database);
        Onyx.setAPI(new BungeeImpl());
        PunishManager.init();
        getProxy().getPluginManager().registerCommand(this, new AntiAttack());
        getProxy().getPluginManager().registerCommand(this, new Glist());
        getProxy().getPluginManager().registerCommand(this, new Skin());
        getProxy().getPluginManager().registerCommand(this, new TpTo());
        getProxy().getPluginManager().registerCommand(this, new Glist());
        getProxy().getPluginManager().registerCommand(this, new MainCommand());
        Log.getLogger().sendLog("§a加载中...");
        regListener();
        new BroadcastTask();
        Log.getLogger().sendLog("§a已成功加载！ §b版本号" + PluginInfo.getVersion());
    }

    public void reload(){
        this.sql.close();
        String url = MainConfig.cfg.getYml().getString("url");
        String user = MainConfig.cfg.getYml().getString("user");
        String passwd = MainConfig.cfg.getYml().getString("passwd");
        String database = MainConfig.cfg.getYml().getString("database");
        this.sql = new SQLHelper(url, user, passwd, database);
        BroadcastConfig.cfg.reload();
        DataConfig.cfg.reload();
        LobbyConfig.cfg.reload();
        SkinConfig.cfg.reload();
        PunishConfig.cfg.reload();
    }

    @Override
    public void onDisable() {
        for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()){
            PlayerData data = Onyx.getPlayerData(online.getName());
            data.saveData(true);
        }
        sql.close();
        Log.getLogger().sendLog("§c已成功卸载！ §b版本号" + PluginInfo.getVersion());
    }

    private void regConfig(){
        DataConfig.init();
        LobbyConfig.init();
        SkinConfig.init();
        MainConfig.init();
        BroadcastConfig.init();
        PunishConfig.init();
    }

    public static Main getInstance(){
        return instance;
    }

    private void regListener(){
        new DataListener();
        new MessageListener();
        new PunishListener();
    }

}
