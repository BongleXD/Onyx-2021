package net.blastmc.onyx.bungee.listener;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.PlayerData;
import net.blastmc.onyx.api.util.SQLHelper;
import net.blastmc.onyx.bungee.punish.PunishManager;
import net.blastmc.onyx.bungee.api.SkinAPI;
import net.blastmc.onyx.bungee.Main;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DataListener implements Listener {

    public DataListener(){
        Main.getInstance().getProxy().getPluginManager().registerListener(Main.getInstance(), this);
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        PendingConnection conn = e.getConnection();
        PlayerData data = Onyx.getPlayerData(conn.getName());
        if(data == null){
            SkinAPI.getApi().setSkin(e.getConnection(), e.getConnection().getName());
        }else{
            String nickskin = (String) Main.getSQL().getData("player_profile", "pid", data.getPID(), "nick_skin").get(0);
            int i = (int) Main.getSQL().getData("player_profile", "pid", data.getPID(), "nicked").get(0);
            SkinAPI.getApi().setSkin(e.getConnection(), nickskin == null || nickskin.isEmpty() || i == 0 ? e.getConnection().getName() : nickskin);
            PunishManager.getManager().checkBan(data.getPID(), e.getConnection());
            PunishManager.getManager().checkMute(data.getPID());
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        SQLHelper sql = Main.getSQL();
        ProxiedPlayer p = e.getPlayer();
        PlayerData data = Onyx.getPlayerData(p.getName());
        if (data != null) {
            data.setIpLastJoin(e.getPlayer().getAddress().getHostString());
            data.setLastLeaveMills(System.currentTimeMillis());
            data.setStatus("OFFLINE");
            data.setServerJoined("NULL");
            sql.putData("player_data", "pid", data.getPID(), new SQLHelper.SqlValue("ip_last_join", data.getIpLastJoin()));
            data.saveData(true);
        }
    }

    @EventHandler
    public void onConnected(ServerConnectedEvent e) {
        ProxiedPlayer p = e.getPlayer();
        PlayerData data = Onyx.getPlayerData(p.getName());
        if (data != null) {
            if (e.getServer().getInfo().getName().toLowerCase().contains("lobby")) {
                data.setStatus("ONLINE");
            } else {
                data.setStatus("PLAYING");
                data.setRejoinServer(e.getServer().getInfo().getName());
            }
            data.setServerJoined(e.getServer().getInfo().getName());
            data.refreshStaySecs();
            data.saveData(false);
        }
    }

    @EventHandler
    public void onConnect(ServerConnectEvent e) {
        if (e.getTarget().getName().contains("login") || e.isCancelled()) {
            return;
        }
        ProxiedPlayer p = e.getPlayer();
        PlayerData data = Onyx.getPlayerData(p.getName());
        if(data != null){
            data.refreshStaySecs();
            data.setJoinTime(System.currentTimeMillis());
            String nickskin = (String) Main.getSQL().getData("player_profile", "pid", data.getPID(), "nick_skin").get(0);
            int i = (int) Main.getSQL().getData("player_profile", "pid", data.getPID(), "nicked").get(0);
            SkinAPI.getApi().setSkin(e.getPlayer().getPendingConnection(), nickskin == null || nickskin.isEmpty() || i == 0 ? e.getPlayer().getPendingConnection().getName() : nickskin);
            data.setStatus("ONLINE");
            data.setServerJoined(e.getTarget().getName());
            data.saveData(false);
        }
    }

}
