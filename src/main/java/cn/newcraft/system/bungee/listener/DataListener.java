package cn.newcraft.system.bungee.listener;

import cn.newcraft.system.bungee.SkinAPI;
import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bungee.Main;
import cn.newcraft.system.shared.util.SQLHelper;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DataListener implements Listener {

    public DataListener(){
        Main.getInstance().getProxy().getPluginManager().registerListener(Main.getInstance(), this);
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        String nickskin = (String) Main.getSQL().getData("pid", (String) Main.getSQL().getData("player_name", e.getConnection().getName(), "player_data", "pid" ), "player_profile", "nick_skin");
        int i = (int) Main.getSQL().getData("pid", (String) Main.getSQL().getData("player_name", e.getConnection().getName(), "player_data", "pid" ), "player_profile", "nicked");
        SkinAPI.getApi().setSkin(e.getConnection(), nickskin == null || nickskin.isEmpty() || i == 0 ? e.getConnection().getName() : nickskin);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e){
        SQLHelper sql = Main.getSQL();
        String pid = (String) sql.getData("player_name", e.getPlayer().getName(), "player_data", "pid" );
        PlayerData data = PlayerData.getData(pid);
        if(data != null){
            data.setIpLastJoin(e.getPlayer().getAddress().getHostString());
            data.setLastLeaveMills(System.currentTimeMillis());
            data.setStatus("OFFLINE");
            data.setServerJoined("NULL");
            sql.putData("player_data", pid, "ip_last_join", data.getIpLastJoin());
            data.saveData(true);
        }
    }

    @EventHandler
    public void onConnected(ServerConnectedEvent e){
        SQLHelper sql = Main.getSQL();
        String pid = (String) sql.getData("player_name", e.getPlayer().getName(), "player_data", "pid" );
        if(PlayerData.getData(pid) != null){
            PlayerData data = PlayerData.getData(pid);
            if(e.getServer().getInfo().getName().toLowerCase().contains("lobby")) {
                data.setStatus("ONLINE");
            }else{
                data.setStatus("PLAYING");
                data.setRejoinServer(e.getServer().getInfo().getName());
            }
            data.setServerJoined(e.getServer().getInfo().getName());
            data.refreshStaySecs();
            data.saveData(false);
        }
    }

    @EventHandler
    public void onConnect(ServerConnectEvent e){
        SQLHelper sql = Main.getSQL();
        String nickskin = (String) Main.getSQL().getData("pid", (String) sql.getData("player_name", e.getPlayer().getName(), "player_data", "pid" ), "player_profile", "nick_skin");
        int i = (int) Main.getSQL().getData("pid", (String) sql.getData("player_name", e.getPlayer().getName(), "player_data", "pid" ), "player_profile", "nicked");
        SkinAPI.getApi().setSkin(e.getPlayer().getPendingConnection(), nickskin == null || nickskin.isEmpty() || i == 0 ? e.getPlayer().getPendingConnection().getName() : nickskin);
        if(e.getTarget().getName().contains("login") || e.isCancelled()){
            return;
        }
        String pid = (String) sql.getData("player_name", e.getPlayer().getName(), "player_data", "pid" );
        if(pid != null){
            PlayerData data = new PlayerData(pid);
            data.setJoinTime(System.currentTimeMillis());
            data.setStatus("ONLINE");
            data.setServerJoined(e.getTarget().getName());
            data.saveData(false);
        }
    }

}
