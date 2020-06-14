package cn.newcraft.system.bungee;

import cn.newcraft.system.bungee.command.Skin;
import cn.newcraft.system.bungee.config.SkinConfig;
import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.shared.PluginInfo;
import cn.newcraft.system.bungee.command.AntiAttack;
import cn.newcraft.system.bungee.command.Glist;
import cn.newcraft.system.bungee.command.TpTo;
import cn.newcraft.system.bungee.config.DataConfig;
import cn.newcraft.system.bungee.config.LobbyConfig;
import cn.newcraft.system.bungee.listener.DataListener;
import cn.newcraft.system.bungee.listener.MessageListener;
import cn.newcraft.system.shared.util.MathHelper;
import cn.newcraft.system.shared.util.SQLHelper;
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
        sql = new SQLHelper("localhost:36109", "root", "Mysql_r53Era_2686chen.", "newcraftsystem");
        PlayerData.putSQL(sql);
        getProxy().getPluginManager().registerCommand(this, new AntiAttack());
        getProxy().getPluginManager().registerCommand(this, new Glist());
        getProxy().getPluginManager().registerCommand(this, new Skin());
        getProxy().getPluginManager().registerCommand(this, new TpTo());getProxy().getPluginManager().registerCommand(this, new Glist());
        BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(PluginInfo.BUNGEE_INFO + " §a加载中..."));
        regListener();
        getProxy().getConsole().sendMessage(PluginInfo.BUNGEE_INFO + " §a已成功加载！ §b版本号" + PluginInfo.getVersion());
    }

    @Override
    public void onDisable() {
        for(ProxiedPlayer online : BungeeCord.getInstance().getPlayers()){
            PlayerData data = PlayerData.getDataFromName(online.getName());
            data.saveData(true);
        }
        getProxy().getConsole().sendMessage(PluginInfo.BUNGEE_INFO + " §c已成功卸载！ §b版本号" + PluginInfo.getVersion());
    }

    private void regConfig(){
        DataConfig.init();
        LobbyConfig.init();
        SkinConfig.init();
    }

    public static Main getInstance(){
        return instance;
    }

    private void regListener(){
        new DataListener();
    }

}
