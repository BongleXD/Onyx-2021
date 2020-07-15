package net.blastmc.onyx.bungee.api;

import net.blastmc.onyx.bungee.Main;
import net.blastmc.onyx.shared.util.SQLHelper;

import java.util.List;
import java.util.UUID;

public class BanData {

    private static SQLHelper sql = Main.getSQL();
    private UUID uuid;
    private String address;
    private String reason;
    private String punisher;
    private String releaser;
    private long bannedTime;
    private long unbanTime;
    private String server;
    private boolean ipBanned;
    private boolean active;

    private BanData(UUID uuid, String address, String reason, String punisher, String releaser, long bannedTime, long unbanTime, String server, boolean ipBanned, boolean active){
        this.uuid = uuid;
        this.address = address;
        this.reason = reason;
        this.punisher = punisher;
        this.releaser = releaser;
        this.bannedTime = bannedTime;
        this.unbanTime = unbanTime;
        this.server = server;
        this.ipBanned = ipBanned;
        this.active = active;
    }

    public static BanData createNewBanData(UUID uuid, String executor, String server){
        return null;
    }

    public static BanData getLatestDataFromUUID(UUID uuid){
        /*
        List<Long> list = sql.getData("SELECT * FROM player_bans WHERE uuid = '" + uuid.toString() + "' AND active = '1'", "banned_time")
                .stream().map(obj -> Long.parseLong((String) obj)).collect(Collectors.toList());
        if(!list.isEmpty()){
            list.sort(Comparator.reverseOrder());
            long bannedTime = list.get(0);
        }
         */
        if(sql.checkDataExists("ban_list", "uuid", uuid.toString())){
            List<Object> list = sql.getData("ban_list", "uuid", uuid.toString(), "ip", "reason", "executor", "releaser", "banned_at", "release_at", "server", "ipban", "active");
            return new BanData(
                    uuid,
                    (String) list.get(0),
                    (String) list.get(1),
                    (String) list.get(2),
                    (String) list.get(3),
                    (long) list.get(4),
                    (long) list.get(5),
                    (String) list.get(6),
                    (boolean) list.get(7),
                    (boolean) list.get(8));
        }
        return null;
    }

    public static void init(){
        sql.create("ban_list",
                new SQLHelper.Value(SQLHelper.ValueType.ID, "id"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "uuid"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "ip"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "reason"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "punisher"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "releaser"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "banned_at"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "release_at"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "server"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "ipban"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "active"));
    }

    private static String secFormat(long sec){
        StringBuilder sb = new StringBuilder();
        int min = 0;
        int hour = 0;
        int day = 0;
        while(sec >= 60){
            sec -= 60;
            min++;
        }
        while(min >= 60){
            min -= 60;
            hour++;
        }
        while(hour >= 24){
            hour -= 24;
            day++;
        }
        if(day != 0){
            sb.append(day).append(" 天, ");
        }
        if(hour != 0){
            sb.append(hour).append(" 小时, ");
        }
        if(min != 0){
            sb.append(min).append(" 分钟, ");
        }
        if(day == 0){
            sb.append(sec).append(" 秒");
        }
        return sb.toString().endsWith(", ") ? sb.toString().substring(0, sb.toString().length() - 2) : sb.toString();
    }

}
