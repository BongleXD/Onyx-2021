package net.blastmc.onyx.bungee;

import net.blastmc.onyx.bungee.api.BanData;
import net.blastmc.onyx.bungee.command.*;
import net.blastmc.onyx.bungee.config.*;
import net.blastmc.onyx.bungee.listener.DataListener;
import net.blastmc.onyx.bungee.listener.MessageListener;
import net.blastmc.onyx.bungee.task.BroadcastTask;
import net.blastmc.onyx.shared.PlayerData;
import net.blastmc.onyx.shared.PluginInfo;
import net.blastmc.onyx.shared.util.Log;
import net.blastmc.onyx.shared.util.SQLHelper;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    private static Main instance;
    private static SQLHelper sql;

    public static SQLHelper getSQL(){
        return sql;
    }

    @Override
    public void onLoad() {
        PluginInfo.init(this.getDescription().getVersion());
        getProxy().getConsole().sendMessage(new TextComponent("§bNewCraftSystem-Bungee §7> §a加载中..."));
    }

    @Override
    public void onEnable() {
        instance = this;
        new MessageListener();
        regConfig();
        sql = new SQLHelper(MainConfig.cfg.getYml().getString("url"),
                MainConfig.cfg.getYml().getString("user"),
                MainConfig.cfg.getYml().getString("passwd"),
                MainConfig.cfg.getYml().getString("database"));
        PlayerData.putSQL(sql);
        BanData.init();
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

    @Override
    public void onDisable() {
        for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()){
            PlayerData data = PlayerData.getDataFromName(online.getName());
            data.saveData(true);
        }
        Log.getLogger().sendLog("§c已成功卸载！ §b版本号" + PluginInfo.getVersion());
    }

    private void regConfig(){
        DataConfig.init();
        LobbyConfig.init();
        SkinConfig.init();
        MainConfig.init();
        BroadcastConfig.init();
    }

    public static Main getInstance(){
        return instance;
    }

    private void regListener(){
        new DataListener();
    }

}
