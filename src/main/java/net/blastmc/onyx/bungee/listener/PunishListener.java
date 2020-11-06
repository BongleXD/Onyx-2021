package net.blastmc.onyx.bungee.listener;

import net.blastmc.onyx.bungee.Main;
import net.blastmc.onyx.bungee.punish.Mute;
import net.blastmc.onyx.bungee.punish.PunishManager;
import net.blastmc.onyx.bungee.punish.PunishType;
import net.blastmc.onyx.shared.PlayerData;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PunishListener implements Listener {

    public PunishListener(){
        Main.getInstance().getProxy().getPluginManager().registerListener(Main.getInstance(), this);
    }

    private String longToTime(long time){
        StringBuilder sb = new StringBuilder();
        int mins = (int) (time / 1000) / 60;
        int hours = mins / 60;
        int days = hours / 24;
        if (days > 0) {
            sb.append(days).append(" 天").append(hours > 0 ? "," + hours + " 小时" : "").append(mins > 0 ? ", " + mins + " 分钟" : "");
        } else if (hours > 0) {
            sb.append(hours).append(" 小时").append(mins > 0 ? ", " + mins + " 分钟" : "");
        } else if (mins > 0) {
            sb.append(mins).append(" 分钟");
        }
        if(!sb.toString().isEmpty()){
            return sb.toString();
        }
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatEvent e){
        if(e.getSender() instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            PlayerData data = PlayerData.getDataFromName(p.getName());
            if(data != null){
                Mute mute = (Mute) PunishManager.muteData.get(data.getPID());
                if(mute != null){
                    long duration = mute.getDuration();
                    p.sendMessage("§c§m---------------------------");
                    p.sendMessage("§c你已经被此服务器" + (duration <= -1 ? "永久" : "") + "禁言!" + (duration <= -1 ? "还有 §e" + longToTime(mute.getPunishTimeMillis() + duration - System.currentTimeMillis()) + " §c解除禁言！" : ""));
                    p.sendMessage("§f原因: " + mute.getReason());
                    p.sendMessage("§f禁言 ID: " + mute.getPunishID());
                    p.sendMessage("§f如对此禁言不满，请前往 QQ 群 申诉");
                    p.sendMessage("§c§m---------------------------");
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onReceive(PluginMessageEvent e){
        if(e.getTag().equalsIgnoreCase("BungeeCord")){
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
            try {
                String channel = in.readUTF();
                if (channel.equals("BAN_PLAYER")) {
                    String pid = in.readUTF();
                    String executor = in.readUTF();
                    String reason = in.readUTF();
                    String duration = in.readUTF();
                    String server = PlayerData.getOfflineName(pid) == null
                            ? "NULL"
                            : BungeeCord.getInstance().getPlayer(PlayerData.getOfflineName(pid)) == null
                            ? "NULL" : BungeeCord.getInstance().getPlayer(PlayerData.getOfflineName(pid)).getServer().getInfo().getName();
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(executor);
                    if(p != null){
                        p.sendMessage("§a封禁成功！");
                    }
                    PunishManager.getManager().punishPlayer(PunishType.BAN, pid, executor, reason, server, Long.parseLong(duration));
                }
                if (channel.equals("KICK_PLAYER")) {
                    String pid = in.readUTF();
                    String executor = in.readUTF();
                    String reason = in.readUTF();
                    String server = PlayerData.getOfflineName(pid) == null
                            ? "NULL"
                            : BungeeCord.getInstance().getPlayer(PlayerData.getOfflineName(pid)) == null
                            ? "NULL" : BungeeCord.getInstance().getPlayer(PlayerData.getOfflineName(pid)).getServer().getInfo().getName();
                    PunishManager.getManager().punishPlayer(PunishType.KICK, pid, executor, reason, server, -1);
                }
                if (channel.equals("MUTE_PLAYER")) {
                    String pid = in.readUTF();
                    String executor = in.readUTF();
                    String reason = in.readUTF();
                    String duration = in.readUTF();
                    String server = PlayerData.getOfflineName(pid) == null
                            ? "NULL"
                            : BungeeCord.getInstance().getPlayer(PlayerData.getOfflineName(pid)) == null
                            ? "NULL" : BungeeCord.getInstance().getPlayer(PlayerData.getOfflineName(pid)).getServer().getInfo().getName();
                    PunishManager.getManager().punishPlayer(PunishType.MUTE, pid, executor, reason, server, Long.parseLong(duration));
                }
                if (channel.equals("WARN_PLAYER")) {
                    String pid = in.readUTF();
                    String executor = in.readUTF();
                    String reason = in.readUTF();
                    String server = PlayerData.getOfflineName(pid) == null
                            ? "NULL"
                            : BungeeCord.getInstance().getPlayer(PlayerData.getOfflineName(pid)) == null
                            ? "NULL" : BungeeCord.getInstance().getPlayer(PlayerData.getOfflineName(pid)).getServer().getInfo().getName();
                    PunishManager.getManager().punishPlayer(PunishType.WARN, pid, executor, reason, server, -1);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
