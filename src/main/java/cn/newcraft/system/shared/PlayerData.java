package cn.newcraft.system.shared;

import cn.newcraft.system.shared.util.SQLHelper;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    private static SQLHelper sql;
    private String pid;
    private String uuid;
    private String name;
    private String ipLastJoin;
    private String lastLeaveMills;
    private String rejoinServer;
    private String guild;
    private String status;
    private String serverJoined;
    private long joinTime;
    private int staySecs;
    private static HashMap<String, PlayerData> dataMap = new HashMap<>();

    public PlayerData(String pid, String uuid, String name, String ipLastJoin, String lastLeaveMills, String rejoinServer, String guild, String status, String serverJoined, int staySecs){
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
        dataMap.put(pid, this);
    }

    public PlayerData(String pid) {
        this.pid = pid;
        putData();
        dataMap.put(pid, this);
    }

    public static void putSQL(SQLHelper sqlHelper){
        sql = sqlHelper;
    }

    public static void init() {
        sql.create("player_data");
        sql.addStringColumn("player_data", "pid");
        sql.addStringColumn("player_data", "uuid");
        sql.addStringColumn("player_data", "player_name");
        sql.addStringColumn("player_data", "ip_last_join");
        sql.addStringColumn("player_data", "last_leave_mills");
        sql.addIntegerColumn("player_data", "stay_secs");
        sql.addStringColumn("player_data", "rejoin_server");
        sql.addStringColumn("player_data", "guild");
        sql.addStringColumn("player_data", "status");
        sql.addStringColumn("player_data", "server_joined");
    }

    private void putData() {
        this.uuid = (String) sql.getData("pid", pid, "player_data", "uuid" );
        this.name = (String) sql.getData("pid", pid, "player_data", "player_name");
        this.ipLastJoin = (String) sql.getData("pid", pid, "player_data", "ip_last_join");
        this.lastLeaveMills = (String) sql.getData("pid", pid, "player_data", "last_leave_mills");
        this.rejoinServer = (String) sql.getData("pid", pid, "player_data", "rejoin_server");
        this.guild = (String) sql.getData("pid", pid, "player_data", "guild");
        this.status = (String) sql.getData("pid", pid, "player_data", "status");
        this.serverJoined = (String) sql.getData("pid", pid, "player_data", "server_joined");
        this.staySecs = (int) sql.getData("pid", pid, "player_data", "stay_secs");
    }

    public String getPID(){
        return pid;
    }

    public String getUUID() {
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
        int sec = Math.toIntExact((System.currentTimeMillis() - joinTime) / 1000) + staySecs;
        this.joinTime = System.currentTimeMillis();
        this.staySecs = sec;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    public static PlayerData getData(String pid){
        return dataMap.getOrDefault(pid, null);
    }

    public static PlayerData getDataFromUUID(UUID uuid){
        return dataMap.getOrDefault(sql.getData("uuid", uuid.toString(), "player_data", "pid"), null);
    }

    public static PlayerData getDataFromName(String name){
        return dataMap.getOrDefault(sql.getData("player_name", name, "player_data", "pid"), null);
    }

    public static void checkDataFromUUID(UUID uuid){
        String pid = (String) sql.getData("uuid", uuid.toString(), "player_data", "pid");
        if(pid != null) {
            new PlayerData(pid);
        }
    }

    public static void checkDataFromName(String name){
        String pid = (String) sql.getData("player_name", name, "player_data", "pid");
        if(pid != null) {
            new PlayerData(pid);
        }
    }

    public void saveData(boolean destroy){
        refreshStaySecs();
        sql.putData("player_data", this.pid, "uuid", this.uuid);
        sql.putData("player_data", this.pid, "player_name", this.name);
        sql.putData("player_data", this.pid, "ip_last_join", this.ipLastJoin);
        sql.putData("player_data", this.pid, "last_leave_mills", this.lastLeaveMills);
        sql.putData("player_data", this.pid, "rejoin_server", this.rejoinServer);
        sql.putData("player_data", this.pid, "guild", this.guild);
        sql.putData("player_data", this.pid, "status", this.status);
        sql.putData("player_data", this.pid, "server_joined", this.serverJoined);
        sql.putData("player_data", this.pid, "stay_secs", this.getStaySecs());
        if(destroy){
            dataMap.remove(pid);
        }
    }

}
