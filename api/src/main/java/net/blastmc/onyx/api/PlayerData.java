package net.blastmc.onyx.api;

import net.blastmc.onyx.api.util.SQLHelper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private static SQLHelper sql = Onyx.getAPI().getSQL();
    private String pid;
    private UUID uuid;
    private String name;
    private String ipLastJoin;
    private String lastLeaveMills;
    private String rejoinServer;
    private String guild;
    private String status;
    private String serverJoined;
    private long joinTime;
    private int staySecs;

    public PlayerData(String pid, UUID uuid, String name, String ipLastJoin, String lastLeaveMills, String rejoinServer, String guild, String status, String serverJoined, int staySecs){
        this.pid = pid;
        this.uuid = uuid;
        this.name = name;
        this.ipLastJoin = ipLastJoin;
        this.lastLeaveMills = lastLeaveMills;
        this.rejoinServer = rejoinServer;
        this.guild = guild;
        this.status = status;
        this.serverJoined = serverJoined;
        this.staySecs = staySecs;
        Onyx.dataMap.put(pid, this);
    }

    public PlayerData(String pid) {
        this.pid = pid;
        putData();
        Onyx.pidMap.put(uuid.toString(), this.pid);
        Onyx.dataMap.put(pid, this);
    }

    public static void init() {
        sql.create("player_data",
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "pid"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "uuid"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "name"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "ip_last_join"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "last_leave_mills"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "stay_secs"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "rejoin_server"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "guild"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "status"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "server_joined")
        );
    }

    private void putData() {
        List list = sql.getData("player_data", "pid", pid,
                "uuid",
                "name",
                "ip_last_join",
                "last_leave_mills",
                "rejoin_server",
                "guild",
                "status",
                "server_joined",
                "stay_secs");
        this.uuid = UUID.fromString((String) list.get(0));
        this.name = (String) list.get(1);
        this.ipLastJoin = (String) list.get(2);
        this.lastLeaveMills = (String) list.get(3);
        this.rejoinServer = (String) list.get(4);
        this.guild = (String) list.get(5);
        this.status = (String) list.get(6);
        this.serverJoined = (String) list.get(7);
        this.staySecs = (int) list.get(8);
    }

    public String getPID(){
        return pid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpLastJoin() {
        return ipLastJoin;
    }

    public void setIpLastJoin(String ipLastJoin) {
        this.ipLastJoin = ipLastJoin;
    }

    public long getLastLeaveMills() {
        return Long.parseLong(lastLeaveMills);
    }

    public void setLastLeaveMills(long lastLeaveMills) {
        this.lastLeaveMills = String.valueOf(lastLeaveMills);
    }

    public String getRejoinServer() {
        return rejoinServer;
    }

    public void setRejoinServer(String rejoinServer) {
        this.rejoinServer = rejoinServer;
    }

    public String getGuild() {
        return guild;
    }

    public void setGuild(String guild) {
        this.guild = guild;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServerJoined() {
        return serverJoined;
    }

    public void setServerJoined(String serverJoined) {
        this.serverJoined = serverJoined;
    }

    public int getStaySecs() {
        refreshStaySecs();
        return staySecs;
    }

    public void setStaySecs(int staySecs) {
        this.staySecs = staySecs;
    }

    public void refreshStaySecs(){
        if (joinTime == 0) return;
        long sec = (System.currentTimeMillis() - joinTime) / 1000 + staySecs;
        this.joinTime = System.currentTimeMillis();
        this.staySecs = new BigDecimal(sec).intValue();
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    public void saveData(boolean destroy){
        refreshStaySecs();
        sql.putData("player_data", "pid", this.pid,
                new SQLHelper.SqlValue("uuid", this.uuid.toString()),
                new SQLHelper.SqlValue("name", this.name),
                new SQLHelper.SqlValue("ip_last_join", this.ipLastJoin),
                new SQLHelper.SqlValue("last_leave_mills", this.lastLeaveMills),
                new SQLHelper.SqlValue("rejoin_server", this.rejoinServer),
                new SQLHelper.SqlValue("guild", this.guild),
                new SQLHelper.SqlValue("status", this.status),
                new SQLHelper.SqlValue("server_joined", this.serverJoined),
                new SQLHelper.SqlValue("stay_secs", this.getStaySecs()));
        if(destroy){
            destroy();
        }
    }

    public void destroy(){
        Onyx.dataMap.remove(pid);
        Onyx.pidMap.remove(uuid.toString());
        Onyx.pidMap.remove(name);
    }

}
