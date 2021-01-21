package net.blastmc.onyx.bukkit.api;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.clip.placeholderapi.PlaceholderAPI;
import net.blastmc.onyx.api.API;
import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.PlayerData;
import net.blastmc.onyx.api.bukkit.NMS;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.Rank;
import net.blastmc.onyx.api.bukkit.event.PlayerDataCreateEvent;
import net.blastmc.onyx.api.bukkit.server.ServerType;
import net.blastmc.onyx.api.util.SQLHelper;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.admin.Vanish;
import net.blastmc.onyx.bukkit.config.TagConfig;
import net.blastmc.onyx.bukkit.util.Method;
import net.blastmc.onyx.api.bukkit.TeamAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class BukkitAPI implements API {

    static HashMap<String, OnyxPlayerProfile> dataMap = new HashMap<>();

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
                Connection conn = getSQL().getConnection();
                Statement stmt = conn.createStatement();
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
                conn.close();
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
        Bukkit.getPluginManager().callEvent(new PlayerDataCreateEvent(data));
    }

    @Override
    public void refreshVanish(UUID uuid){
        Player p = Bukkit.getPlayer(uuid);
        if (!TagConfig.ENABLED && TagConfig.ENABLED_WORLD.contains(p.getWorld().getName())) {
            PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
            if (prof == null) {
                return;
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerProfile other = Onyx.getPlayerProfile(online.getUniqueId());
                if(other == null){
                    continue;
                }
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, Method.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, other));
                //restore tag
                String suffix = PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                String priority = Method.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
                if (p != online) {
                    String otherSuffix = PlaceholderAPI.setPlaceholders(online, Method.getTagData(online).getSuffix());
                    if (other.isVanish()) {
                        otherSuffix = " §c[已隐身]";
                    }
                    Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, Method.getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, otherSuffix), TeamAction.CREATE, Method.getTagPriority(online, other));
                }
            }
        }
        for (PlayerProfile prof : Vanish.vanishList) {
            if (prof.isVanish()) {
                Method.vanishPlayer(Bukkit.getPlayer(prof.getUUID()), p, true);
            }
        }
    }

    @Override
    public NMS getNMS() {
        return Main.getNMS();
    }

    @Override
    public PlayerProfile getPlayerProfile(UUID uuid) {
        return dataMap.getOrDefault(getPlayerData(uuid).getPID(), null);
    }

    @Override
    public PlayerProfile getPlayerProfile(String name) {
        return dataMap.getOrDefault(getPlayerData(name).getPID(), null);
    }

    @Override
    public PlayerProfile getPlayerProfileFromPID(String pid) {
        return dataMap.getOrDefault(getPlayerDataFromPID(pid).getPID(), null);
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
        for (Player p : Bukkit.getOnlinePlayers()) {
            refreshVanish(p.getUniqueId());
        }
    }

    @Override
    public String getServerName(){
        return Main.getServerName();
    }

    @Override
    public ServerType getType(){
        return Main.getType();
    }

    @Override
    public void kickToLobby(UUID uuid, String server, String name, String reason) {
        Player p = Bukkit.getPlayer(uuid);
        p.sendMessage("");
        p.sendMessage("§c你的网络连接出现小问题，所以你被传到 §e" + name + " §c中！");
        p.sendMessage("§f原因: " + reason);
        p.sendMessage("");
        ByteArrayDataOutput b = ByteStreams.newDataOutput();
        b.writeUTF("BACK_LOBBY");
        b.writeUTF(server);
        b.writeUTF(Onyx.getPlayerData(uuid).getName());
        p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }

    @Override
    public void refreshTag(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (TagConfig.ENABLED && TagConfig.ENABLED_WORLD.contains(p.getWorld().getName())) {
            p.setDisplayName(PlaceholderAPI.setPlaceholders(p, "%profile_prefix%") + p.getName() + PlaceholderAPI.setPlaceholders(p, "%profile_suffix%"));
            PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
            String priority = Method.getTagPriority(p, prof);
            String suffix = PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getSuffix());
            if (prof.isVanish()) {
                suffix = " §c[已隐身]";
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, priority);
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
            }
        }else{
            removeTag(p.getUniqueId());
        }
    }

    @Override
    public void removeTag(UUID uuid) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerProfile prof = Onyx.getPlayerProfile(online.getUniqueId());
            String priority = Method.getTagPriority(online, prof);
            Main.getNMS().changeNameTag(online, Bukkit.getPlayer(uuid), "", "", TeamAction.DESTROY, priority);
        }
    }

    @Override
    public void refreshAllTag(){
        for(Player online : Bukkit.getOnlinePlayers()){
            refreshTag(online.getUniqueId());
        }
    }

    @Override
    public void refreshTagFor(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
        if (prof != null && TagConfig.ENABLED && TagConfig.ENABLED_WORLD.contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerProfile other = Onyx.getPlayerProfile(online.getUniqueId());
                if (other == null) {
                    continue;
                }
                String suffix = PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                String priority = Method.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, priority);
                Main.getNMS().changeNameTag(online, p,
                        PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getPrefix()),
                        suffix, TeamAction.CREATE, priority);
                if (p != online) {
                    String otherSuffix = PlaceholderAPI.setPlaceholders(online, Method.getTagData(online).getSuffix());
                    if (other.isVanish()) {
                        otherSuffix = " §c[已隐身]";
                    }
                    Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, other));
                    Main.getNMS().changeNameTag(p, online,
                            PlaceholderAPI.setPlaceholders(online, Method.getTagData(online).getPrefix()),
                            PlaceholderAPI.setPlaceholders(online, otherSuffix),
                            TeamAction.CREATE, Method.getTagPriority(online, other));
                }
            }
        }
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
        Bukkit.getPluginManager().callEvent(new PlayerDataCreateEvent(data));
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
        return OnyxRank.getData(name);
    }

}
