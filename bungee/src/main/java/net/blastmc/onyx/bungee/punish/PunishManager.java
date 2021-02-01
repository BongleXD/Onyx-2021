package net.blastmc.onyx.bungee.punish;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.bungee.Main;
import net.blastmc.onyx.bungee.config.PunishConfig;
import net.blastmc.onyx.bungee.utils.Method;
import net.blastmc.onyx.api.utils.SQLHelper;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class PunishManager {

    public static HashMap<String, Punish> muteData = new HashMap<>();
    private static PunishManager manager = new PunishManager();
    private static SQLHelper sql = Main.getSQL();

    public static PunishManager getManager() {
        return manager;
    }

    public static void init() {
        sql.create("ban_data",
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "pid"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "executor"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "server"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "ban_millis"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "duration"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "reason"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "ban_id"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "action"));
        sql.create("mute_data",
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "pid"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "executor"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "server"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "mute_millis"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "duration"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "reason"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "mute_id"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "action"));
        sql.create("warn_data",
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "pid"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "executor"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "server"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "warn_millis"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "reason"));
        sql.create("kick_data",
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "pid"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "executor"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "server"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "kick_millis"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "reason"));
    }

    public void checkBan(String pid, PendingConnection player) {
        if (sql.checkDataExists("ban_data", "pid", pid)) {
            try {
                Connection conn = sql.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select ban_millis, action, duration, reason, ban_id from ban_data where pid = '" + pid + "' order by ban_millis desc limit 1;");
                if (rs.next()) {
                    if (!rs.getString("action").equals("UNBAN")) {
                        long duration = Long.parseLong(rs.getString("duration"));
                        long banMillis = Long.parseLong(rs.getString("ban_millis"));
                        if (duration + banMillis <= System.currentTimeMillis() && duration != -1) {
                            return;
                        }
                        String reason = rs.getString("reason");
                        String banID = rs.getString("ban_id");
                        player.disconnect("§c你已经被此服务器" + (duration <= -1 ? "永久" : "") + "封禁！ " + (duration >= 0 ? "还有 §f" + Method.longToTime(banMillis + duration - System.currentTimeMillis()) + " §c解除封禁！" : "") + "\n\n" +
                                "§7原因: §f" + reason + "\n" +
                                "§7了解更多: §b§n764575479\n\n" +
                                "§7封禁 ID: §f§o#§f" + banID + "\n" +
                                "§7向客服分享你的封禁 ID 可能会加快处理此封禁的速度！");
                    }
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    public void checkMute(String pid){
        if(sql.checkDataExists("mute_data", "pid", pid)){
            try {
                Connection conn = sql.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select mute_millis, action, duration, reason, mute_id from mute_data where pid = '" + pid + "' order by mute_millis desc limit 1;");
                if(rs.next()){
                    if(!rs.getString("action").equals("UNMUTE")){
                        long duration = Long.parseLong(rs.getString("duration"));
                        long muteMillis = Long.parseLong(rs.getString("mute_millis"));
                        String reason = rs.getString("reason");
                        String muteId = rs.getString("mute_id");
                        Mute mute = new Mute("NULL", "NULL", muteMillis, duration, muteId, reason);
                        muteData.put(pid, mute);
                    }
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    private void releaseSyncData(String pid, Punish punish){
        if (Ban.class.equals(punish.getClass())) {
            Ban ban = (Ban) punish;
            sql.insertData("ban_data",
                    new SQLHelper.SqlValue("pid", pid),
                    new SQLHelper.SqlValue("executor", ban.getExecutorName()),
                    new SQLHelper.SqlValue("reason", ban.getReason()),
                    new SQLHelper.SqlValue("ban_millis", ban.getPunishTimeMillis()),
                    new SQLHelper.SqlValue("action", "UNBAN")
            );
        } else if (Mute.class.equals(punish.getClass())) {
            Mute mute = (Mute) punish;
            sql.insertData("mute_data",
                    new SQLHelper.SqlValue("pid", pid),
                    new SQLHelper.SqlValue("executor", mute.getExecutorName()),
                    new SQLHelper.SqlValue("reason", mute.getReason()),
                    new SQLHelper.SqlValue("mute_millis", mute.getPunishTimeMillis()),
                    new SQLHelper.SqlValue("action", "UNMUTE")
            );
        }
    }

    private void punishSyncData(String pid, Punish punish){
        if (punish instanceof Ban) {
            Ban ban = (Ban) punish;
            sql.insertData("ban_data",
                    new SQLHelper.SqlValue("pid", pid),
                    new SQLHelper.SqlValue("executor", ban.getExecutorName()),
                    new SQLHelper.SqlValue("server", ban.getServer()),
                    new SQLHelper.SqlValue("ban_millis", ban.getPunishTimeMillis()),
                    new SQLHelper.SqlValue("duration", ban.getDuration() < -1 ? -1 : ban.getDuration()),
                    new SQLHelper.SqlValue("reason", ban.getReason()),
                    new SQLHelper.SqlValue("ban_id", ban.getPunishID()),
                    new SQLHelper.SqlValue("action", "BAN")
            );
        } else if (punish instanceof Mute) {
            Mute mute = (Mute) punish;
            sql.insertData("mute_data",
                    new SQLHelper.SqlValue("pid", pid),
                    new SQLHelper.SqlValue("executor", mute.getExecutorName()),
                    new SQLHelper.SqlValue("server", mute.getServer()),
                    new SQLHelper.SqlValue("mute_millis", mute.getPunishTimeMillis()),
                    new SQLHelper.SqlValue("duration", mute.getDuration() < -1 ? -1 : mute.getDuration()),
                    new SQLHelper.SqlValue("reason", mute.getReason()),
                    new SQLHelper.SqlValue("mute_id", mute.getPunishID()),
                    new SQLHelper.SqlValue("action", "MUTE")
            );
        } else if (punish instanceof Kick) {
            Kick kick = (Kick) punish;
            sql.insertData("kick_data",
                    new SQLHelper.SqlValue("pid", pid),
                    new SQLHelper.SqlValue("executor", kick.getExecutorName()),
                    new SQLHelper.SqlValue("server", kick.getServer()),
                    new SQLHelper.SqlValue("kick_millis", kick.getPunishTimeMillis()),
                    new SQLHelper.SqlValue("reason", kick.getReason())
            );
        }else if (punish instanceof Warn) {
            Warn warn = (Warn) punish;
            sql.insertData("warn_data",
                    new SQLHelper.SqlValue("pid", pid),
                    new SQLHelper.SqlValue("executor", warn.getExecutorName()),
                    new SQLHelper.SqlValue("server", warn.getServer()),
                    new SQLHelper.SqlValue("warn_millis", warn.getPunishTimeMillis()),
                    new SQLHelper.SqlValue("reason", warn.getReason())
            );
        }
    }

    public void releasePlayer(PunishType type, String pid, String executor, String reason) {
        new Thread(() -> {
            switch (type) {
                case BAN:
                    Ban ban = new Ban(executor, "", System.currentTimeMillis(), 0, "", reason);
                    releaseSyncData(pid, ban);
                case MUTE:
                    Mute mute = new Mute(executor, "", System.currentTimeMillis(), 0, "", reason);
                    releaseSyncData(pid, mute);
                    muteData.remove(pid);
            }
        }).start();
    }

    public void punishPlayer(PunishType type, String pid, String executor, String reason, String server, long duration){
        new Thread(() -> {
            ProxiedPlayer p = Onyx.getPlayerDataFromPID(pid) == null ? null : BungeeCord.getInstance().getPlayer(Onyx.getPlayerDataFromPID(pid).getName());
            switch (type){
                case BAN:
                    Ban ban = new Ban(executor, server, System.currentTimeMillis(), duration, genPunishID(type), reason);
                    punishSyncData(pid, ban);
                    if(p != null){
                        p.disconnect("§c你已经被此服务器" + (duration <= -1 ? "永久" : "") + "封禁！ " + (duration >= 0 ? "还有 §f" + Method.longToTime(ban.getPunishTimeMillis() + duration - System.currentTimeMillis()) + " §c解除封禁！" : "") + "\n\n" +
                                "§7原因: §f" + reason + "\n" +
                                "§7了解更多: §b§n764575479\n\n" +
                                "§7封禁 ID: §f§o#§f" + ban.getPunishID() + "\n" +
                                "§7向客服分享你的封禁 ID 可能会加快处理此封禁的速度！");
                        BungeeCord.getInstance().broadcast(PunishConfig.cfg.getYml().getString("ban.broadcast-message"));
                    }
                    break;
                case WARN:
                    Warn warn = new Warn(executor, server, System.currentTimeMillis(), reason);
                    punishSyncData(pid, warn);
                    int counts = sql.countData("select count(*) from warn_data where pid = '" + pid + "' && warn_millis >= " + (System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 3)) + ";");
                    if(p != null && counts <= 2){
                        p.sendMessage("§c§l警告！ §f你现在" + (reason.isEmpty() ? "" : "因为 §e\"" + reason + "\"§f ") + "被管理员/客服警告！如果你再被警告§c " + (3 - counts) + " §f次将会被封禁 §c7 天§f！如果你对此警告不满请到 §b§n764575479 §f向客服进行反馈！");
                    }else{
                        punishPlayer(PunishType.BAN, pid, executor, "被管理员/客服警告次数过多！", server, 1000 * 60 * 60 * 24 * 7);
                    }
                    break;
                case KICK:
                    Kick kick = new Kick(executor, server, System.currentTimeMillis(), reason);
                    punishSyncData(pid, kick);
                    if(p != null){
                        p.disconnect("§c你已经被从此服务器踢出！\n\n" +
                                "§7原因: §f" + reason + "\n" +
                                "§7了解更多: §b§n764575479\n\n" +
                                "§7如果你对这个踢出不满请向客服进行反馈！");
                    }
                    break;
                case MUTE:
                    Mute mute = new Mute(executor, server, System.currentTimeMillis(), duration, genPunishID(type), reason);
                    punishSyncData(pid, mute);
                    muteData.put(pid, mute);
                    p.sendMessage("§c§m---------------------------");
                    p.sendMessage("§c你已经被此服务器" + (duration <= -1 ? "永久" : "") + "禁言！ " + (duration >= 0 ? "还有 §f" + Method.longToTime(mute.getPunishTimeMillis() + duration - System.currentTimeMillis()) + " §c解除禁言！" : ""));
                    p.sendMessage("§f原因: " + mute.getReason());
                    p.sendMessage("§f禁言 ID: " + mute.getPunishID());
                    p.sendMessage("§f如对此禁言不满，请前往 QQ 群 申诉");
                    p.sendMessage("§c§m---------------------------");
                    break;
            }
        }).start();
    }

    private String genPunishID(PunishType type){
        if(type != PunishType.BAN && type != PunishType.MUTE){
            return null;
        }
        String prefix = type.getPrefix();
        while (true){
            String punishID = prefix + "-" + Method.getRandomString(8);
            if(!sql.checkDataExists(type.getSqlPrefix() + "_data", type.getSqlPrefix() + "_id", punishID)){
                return punishID;
            }
        }
    }

}
