package net.blastmc.onyx.bungee.listener;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.PlayerData;
import net.blastmc.onyx.bungee.Main;
import net.blastmc.onyx.bungee.config.PunishConfig;
import net.blastmc.onyx.bungee.punish.Mute;
import net.blastmc.onyx.bungee.punish.PunishManager;
import net.blastmc.onyx.bungee.punish.PunishType;
import net.blastmc.onyx.bungee.utils.Method;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatEvent e){
        if(e.getSender() instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) e.getSender();
            PlayerData data = Onyx.getPlayerData(p.getName());
            if(data != null){
                Mute mute = (Mute) PunishManager.muteData.get(data.getPID());
                if(mute != null && (!e.getMessage().startsWith("/") || PunishConfig.cfg.getBlackList().stream().anyMatch(s -> e.getMessage().startsWith(s)))){
                    long duration = mute.getDuration();
                    long leftTime = mute.getPunishTimeMillis() + duration - System.currentTimeMillis();
                    if(leftTime <= 0){
                        PunishManager.muteData.remove(data.getPID());
                        return;
                    }
                    p.sendMessage("§c§m---------------------------");
                    p.sendMessage("§c你已经被此服务器" + (duration <= -1 ? "永久" : "") + "禁言！ " + (duration >= 0 ? "还有 §f" + Method.longToTime(mute.getPunishTimeMillis() + duration - System.currentTimeMillis()) + " §c解除禁言！" : ""));
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
                    String server = Onyx.getAPI().getOfflineName(pid) == null
                            ? "NULL"
                            : BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineName(pid)) == null
                            ? "NULL" : BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineName(pid)).getServer().getInfo().getName();
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(executor);
                    ProxiedPlayer other = BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineUUID(pid));
                    if(p != null){
                        if(other != null && other.hasPermission("onyx.punish.bypass")){
                            p.sendMessage("§c无法惩罚该玩家！");
                        }else{
                            p.sendMessage("§a已将玩家 §e" + Onyx.getAPI().getOfflineName(pid) + " §a封禁, 时长 §e" + (Long.parseLong(duration) <= -1 ? "永久" : Method.longToTime(Long.parseLong(duration)))+ "§a！");
                            PunishManager.getManager().punishPlayer(PunishType.BAN, pid, executor, reason, server, Long.parseLong(duration));
                        }
                    }
                }
                if (channel.equals("UNBAN_PLAYER")) {
                    String pid = in.readUTF();
                    String executor = in.readUTF();
                    String reason = in.readUTF();
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(executor);
                    if(p != null){
                        p.sendMessage("§a已将玩家 §e" + Onyx.getAPI().getOfflineName(pid) + " §a的封禁解除！");
                    }
                    PunishManager.getManager().releasePlayer(PunishType.BAN, pid, executor, reason);
                }
                if (channel.equals("KICK_PLAYER")) {
                    String pid = in.readUTF();
                    String executor = in.readUTF();
                    String reason = in.readUTF();
                    String server = Onyx.getAPI().getOfflineName(pid) == null
                            ? "NULL"
                            : BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineName(pid)) == null
                            ? "NULL" : BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineName(pid)).getServer().getInfo().getName();
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(executor);
                    ProxiedPlayer other = BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineUUID(pid));
                    if(p != null){
                        if(other != null && other.hasPermission("onyx.punish.bypass")){
                            p.sendMessage("§c无法惩罚该玩家！");
                        }else {
                            p.sendMessage("§a已将玩家 §e" + Onyx.getAPI().getOfflineName(pid) + " §a移除服务器！");
                            PunishManager.getManager().punishPlayer(PunishType.KICK, pid, executor, reason, server, -1);
                        }
                    }
                }
                if (channel.equals("MUTE_PLAYER")) {
                    String pid = in.readUTF();
                    String executor = in.readUTF();
                    String reason = in.readUTF();
                    String duration = in.readUTF();
                    String server = Onyx.getAPI().getOfflineName(pid) == null
                            ? "NULL"
                            : BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineName(pid)) == null
                            ? "NULL" : BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineName(pid)).getServer().getInfo().getName();
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(executor);
                    ProxiedPlayer other = BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineUUID(pid));
                    if(p != null){
                        if(other != null && other.hasPermission("onyx.punish.bypass")){
                            p.sendMessage("§c无法惩罚该玩家！");
                        }else {
                            p.sendMessage("§a已将玩家 §e" + Onyx.getAPI().getOfflineName(pid) + " §a禁言, 时长 §e" + (Long.parseLong(duration) <= -1 ? "永久" : Method.longToTime(Long.parseLong(duration))) + "§a！");
                            PunishManager.getManager().punishPlayer(PunishType.MUTE, pid, executor, reason, server, Long.parseLong(duration));
                        }
                    }
                }
                if (channel.equals("UNMUTE_PLAYER")) {
                    String pid = in.readUTF();
                    String executor = in.readUTF();
                    String reason = in.readUTF();
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(executor);
                    if(p != null){
                        p.sendMessage("§a已将玩家 §e" + Onyx.getAPI().getOfflineName(pid) + " §a禁言解除！");
                    }
                    PunishManager.getManager().releasePlayer(PunishType.MUTE, pid, executor, reason);
                }
                if (channel.equals("WARN_PLAYER")) {
                    String pid = in.readUTF();
                    String executor = in.readUTF();
                    String reason = in.readUTF();
                    String server = Onyx.getAPI().getOfflineName(pid) == null
                            ? "NULL"
                            : BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineName(pid)) == null
                            ? "NULL" : BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineName(pid)).getServer().getInfo().getName();
                    ProxiedPlayer p = BungeeCord.getInstance().getPlayer(executor);
                    ProxiedPlayer other = BungeeCord.getInstance().getPlayer(Onyx.getAPI().getOfflineUUID(pid));
                    if(p != null){
                        if(other != null && other.hasPermission("onyx.punish.bypass")){
                            p.sendMessage("§c无法惩罚该玩家！");
                        }else {
                            p.sendMessage("§a已警告玩家 §e" + Onyx.getAPI().getOfflineName(pid) + "§a！");
                            PunishManager.getManager().punishPlayer(PunishType.WARN, pid, executor, reason, server, -1);
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
