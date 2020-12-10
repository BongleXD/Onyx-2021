package net.blastmc.onyx.bungee.api;

import net.blastmc.onyx.api.API;
import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.PlayerData;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.Rank;
import net.blastmc.onyx.api.bukkit.server.ServerType;
import net.blastmc.onyx.api.util.SQLHelper;
import net.blastmc.onyx.bungee.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.UUID;

public class BungeeAPI implements API {

    @Override
    public String getPIDIgnoreNick(String name) {
        try {
            return (String) getSQL().getData("player_data", "name", name, "pid").get(0);
        }catch (Exception ex1){
            try{
                return (String) getSQL().getData("player_profile", "nick_name", name, "pid").get(0);
            }catch (Exception ex2){
                return null;
            }
        }
    }

    @Override
    public UUID getOfflineUUID(String pid) {
        try {
            return UUID.fromString((String) getSQL().getData("player_data", "pid", pid, "uuid").get(0));
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    public String getOfflineName(String pid) {
        try {
            return (String) getSQL().getData("player_data", "pid", pid, "name").get(0);
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    public String getOfflinePID(UUID uuid) {
        try {
            return (String) getSQL().getData("player_data", "uuid", uuid.toString(), "pid").get(0);
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    public String getOfflinePID(String name) {
        try {
            return (String) getSQL().getData("player_data", "name", name, "pid").get(0);
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    public boolean isBanned(String pid) {
        if (getSQL().checkDataExists("ban_data", "pid", pid)) {
            try {
                Statement stmt = getSQL().createStatement();
                ResultSet rs = stmt.executeQuery("select ban_millis, action, duration, reason, ban_id from ban_data where pid = '" + pid + "' order by ban_millis desc limit 1;");
                if (rs.next()) {
                    if (!rs.getString("action").equals("UNBAN")) {
                        long duration = Long.parseLong(rs.getString("duration"));
                        long banMillis = Long.parseLong(rs.getString("ban_millis"));
                        if (duration + banMillis <= System.currentTimeMillis() && duration != -1) {
                            return false;
                        }
                        return true;
                    }
                }
                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public String getOfflineTrueDisplayName(UUID uuid){
        try {
            String pid = getOfflinePID(uuid);
            return (String) Main.getSQL().getData("player_profile", "pid", pid, "prefix").get(0) + Main.getSQL().getData("player_data", "uuid", uuid.toString(), "name").get(0) + Main.getSQL().getData("player_profile", "pid", pid, "suffix").get(0);
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    public void createNewData(String pid, UUID uuid, String name) {
        PlayerData data = new PlayerData(
                pid,
                uuid,
                name,
                "",
                "",
                "",
                "",
                "OFFLINE",
                "NULL",
                0);
        data.setJoinTime(System.currentTimeMillis());
        data.saveData(false);
    }

    @Override
    public void refreshVanish(UUID uuid){
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public PlayerProfile getPlayerProfile(UUID uuid) {
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public PlayerProfile getPlayerProfile(String name) {
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public PlayerProfile getPlayerProfileFromPID(String pid) {
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public PlayerData getPlayerData(UUID uuid) {
        return Onyx.getPlayerData(uuid);
    }

    @Override
    public PlayerData getPlayerData(String name) {
        return Onyx.getPlayerData(name);
    }

    @Override
    public PlayerData getPlayerDataFromPID(String pid) {
        return Onyx.getPlayerDataFromPID(pid);
    }

    @Override
    public void refreshAllPlayerVanish() {
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public String getServerName(){
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public ServerType getType(){
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public void kickToLobby(UUID uuid, String server, String name, String reason) {
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public void refreshTag(UUID uuid) {
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public void removeTag(UUID uuid){
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public void refreshAllTag(){
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

    @Override
    public SQLHelper getSQL() {
        return Main.getSQL();
    }

    @Override
    public void createNewData(UUID uuid, String name) {
        String newPid = spawnUniquePID();
        PlayerData data = new PlayerData(
                newPid,
                uuid,
                name,
                "",
                "",
                "",
                "",
                "OFFLINE",
                "NULL",
                0);
        Onyx.addPID(uuid.toString(), newPid);
        Onyx.addPID(name, newPid);
        data.setJoinTime(System.currentTimeMillis());
        data.saveData(false);
    }

    private String[] randomLetters = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    private String generate(){
        StringBuilder sb = new StringBuilder();
        for(int j = 0; j < 16; j++){
            if(j == 0){
                sb.append("BM");
            }
            if(j % 4 == 0){
                sb.append("-");
            }
            sb.append(randomLetters[new Random().nextInt(35)]);
        }
        return sb.toString();
    }

    @Override
    public String spawnUniquePID(){
        while(true){
            String pid = generate();
            if(!Main.getSQL().checkDataExists("player_data", "pid", pid)){
                return pid;
            }
        }
    }

    @Override
    public String getAPIVersion() {
        return "1.6";
    }

    @Override
    public Rank getRank(String name) {
        throw new UnsupportedOperationException("Please use this method on PotatoSpigot Server");
    }

}
