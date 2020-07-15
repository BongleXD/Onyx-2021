package net.blastmc.onyx.bungee.listener;

import net.blastmc.onyx.bungee.Main;
import net.blastmc.onyx.bungee.SkinAPI;
import net.blastmc.onyx.bungee.config.DataConfig;
import net.blastmc.onyx.bungee.config.LobbyConfig;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MessageListener implements Listener {

    public MessageListener(){
        Main.getInstance().getProxy().getPluginManager().registerListener(Main.getInstance(), this);
    }

    @EventHandler
    public void onReceive(PluginMessageEvent e){
        if(e.getTag().equalsIgnoreCase("BungeeCord")){
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
            try {
                String channel = in.readUTF();
                if (channel.equals("INFO")) {
                    String player = in.readUTF();
                    List<String> whiteList = DataConfig.cfg.getYml().getStringList("white-names");
                    if (whiteList.contains(player)) {
                        Main.getInstance().getProxy().getConsole().sendMessage("§e玩家 §b" + player + " §e拥有白名单已绕过IP检测！");
                        return;
                    }
                    String input = in.readUTF();
                    if (input.equals("0")) {
                        String ip = in.readUTF();
                        DataConfig.cfg.addIP(ip);
                        Main.getInstance().getProxy().getConsole().sendMessage("§c§lANTI ATTACK! §7检测到IP §c" + ip + " §7存在异常行为已加入至黑名单！");
                        e.getSender().disconnect("§c§lANTI ATTACK! §7检测到你的 IP 连接异常，请联系管理员解除异常状态\n§bQQ群：764575479");
                    }
                }
                if (channel.equals("WHITE")){
                    String playerName = in.readUTF();
                    DataConfig.cfg.addWhitePlayers(playerName);
                }
                if (channel.equals("BACK_LOBBY")) {
                    String lobby = in.readUTF();
                    String playerName = in.readUTF();
                    Map<String, ServerInfo> servers = BungeeCord.getInstance().getServers();
                    List<String> list = LobbyConfig.cfg.getYml().getStringList("lobby." + lobby);
                    if(!list.isEmpty()){
                        serverInfo(playerName, servers, list);
                        return;
                    }
                }
                if (channel.equals("CHANGE_SKIN")) {
                    String playerName = in.readUTF();
                    String skinName = in.readUTF();
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(playerName);
                    SkinAPI.getApi().setSkin(p.getPendingConnection(), skinName);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void serverInfo(String playerName, Map<String, ServerInfo> servers, List<String> list) {
        ServerInfo server = BungeeCord.getInstance().getServerInfo(list.get(0));
        int onlines = BungeeCord.getInstance().getServerInfo(list.get(0)).getPlayers().size();
        for (String s : list) {
            ServerInfo info = servers.get(s);
            if (onlines > info.getPlayers().size()) {
                server = info;
            }
            onlines = Math.min(onlines, info.getPlayers().size());
        }
        if (server == null) {
            return;
        }
        Main.getInstance().getProxy().getPlayer(playerName).sendMessage("§a正在将你传送至大厅 §7" + server.getName());
        Main.getInstance().getProxy().getPlayer(playerName).connect(server);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onConnect(ServerConnectEvent e){
        List<String> ipList = DataConfig.cfg.getYml().getStringList("black-ip");
        if(ipList.contains(e.getPlayer().getAddress().getAddress().getHostAddress())){
            e.getPlayer().disconnect("§c§lANTI ATTACK! §7检测到你的 IP 连接异常，请联系管理员解除异常状态\n§bQQ群：764575479");
            Main.getInstance().getProxy().getConsole().sendMessage("§c§lANTI ATTACK! §7检测到黑名单 IP §c" + e.getPlayer().getAddress().getAddress().getHostAddress() + " §7试图加入服务器，已被拒绝！");
            e.setCancelled(true);
        }
    }
}
