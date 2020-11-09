package net.blastmc.onyx.bukkit.api;

import net.blastmc.onyx.bukkit.api.event.PlayerDataCreateEvent;
import net.blastmc.onyx.bukkit.util.BukkitMethod;
import net.blastmc.onyx.bukkit.util.TeamAction;
import net.blastmc.onyx.bukkit.proxy.ServerType;
import net.blastmc.onyx.shared.PlayerData;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.config.TagConfig;
import net.blastmc.onyx.bukkit.support.NMS;
import net.blastmc.onyx.shared.util.SQLHelper;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.UUID;

public class Onyx {

    private final static Onyx api = new Onyx();

    public static Onyx getApi() {
        return api;
    }

    public static SQLHelper getSQL(){
        return Main.getSQL();
    }

    public String getPID(String name) {
        try {
            return (String) getSQL().getData("player_data", "name", name, "pid").get(0);
        }catch (Exception ex){
            return null;
        }
    }

    public boolean checkBan(String pid) {
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

    public String getOfflineTrueDisplayName(UUID uuid){
        try {
            String pid = Onyx.getApi().getPID(uuid);
            return (String) Main.getSQL().getData("player_profile", "pid", pid, "prefix").get(0) + Main.getSQL().getData("player_data", "uuid", uuid.toString(), "name").get(0) + Main.getSQL().getData("player_profile", "pid", pid, "suffix").get(0);
        }catch (Exception ex){
            return null;
        }
    }

    public String getPID(UUID uuid) {
        try {
            return (String) getSQL().getData("player_data", "uuid", uuid.toString(), "pid").get(0);
        }catch (Exception ex){
            return null;
        }
    }

    public void refreshVanishAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            refreshVanishAll(p);
        }
    }

    public void refreshVanishAll(Player p){
        if (!TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
            if (prof == null) {
                return;
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerProfile other = PlayerProfile.getDataFromUUID(online.getUniqueId());
                if(other == null){
                    continue;
                }
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, BukkitMethod.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, BukkitMethod.getTagPriority(online, other));
                //restore tag
                String suffix = PlaceholderAPI.setPlaceholders(p, BukkitMethod.getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                String priority = BukkitMethod.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, BukkitMethod.getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
                if (p != online) {
                    String otherSuffix = PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getSuffix());
                    if (other.isVanish()) {
                        otherSuffix = " §c[已隐身]";
                    }
                    Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, otherSuffix), TeamAction.CREATE, BukkitMethod.getTagPriority(online, other));
                }
            }
        }
        for (PlayerProfile prof : PlayerProfile.getVanishs()) {
            if (prof.isVanish()) {
                BukkitMethod.vanishPlayer(Bukkit.getPlayer(prof.getUUID()), p, true);
            }
        }
    }

    public String getServerName(){
        return Main.getServerName();
    }

    public ServerType getType(){
        return Main.getType();
    }

    public void kickToLobby(Plugin plugin, Player p, String server, String name, String reason){
        p.sendMessage("");
        p.sendMessage("§c你的网络连接出现小问题，所以你被传到 §e" + name + " §c中！");
        p.sendMessage("§f原因: " + reason);
        p.sendMessage("");
        ByteArrayDataOutput b = ByteStreams.newDataOutput();
        b.writeUTF("BACK_LOBBY");
        b.writeUTF(server);
        b.writeUTF(PlayerData.getDataFromUUID(p.getUniqueId()).getName());
        p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

    public NMS getNMS(){
        return Main.getNMS();
    }

    public void refreshTag(Player p) {
        if (TagConfig.cfg.getBoolean("enabled")) {
            p.setDisplayName(PlaceholderAPI.setPlaceholders(p, "%profile_prefix%") + p.getName() + PlaceholderAPI.setPlaceholders(p, "%profile_suffix%"));
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerProfile prof = PlayerProfile.getDataFromUUID(online.getUniqueId());
                String priority = BukkitMethod.getTagPriority(online, prof);
                String suffix = PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, priority);
                Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getPrefix()), suffix, TeamAction.CREATE, priority);
            }
        }else{
            removeTag(p);
        }
    }

    public void removeTag(Player p){
        for (Player online : Bukkit.getOnlinePlayers()) {
            Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, "0");
        }
    }

    public void refreshAllTag(){
        for(Player online : Bukkit.getOnlinePlayers()){
            refreshTag(online);
        }
    }

    public void createPIDExists(String pid, UUID uuid, String name) {
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
        PlayerData.addPID(uuid.toString(), newPid);
        PlayerData.addPID(name, newPid);
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

    public String spawnUniquePID(){
        while(true){
            String pid = generate();
            if(!Main.getSQL().checkDataExists("player_data", "pid", pid)){
                return pid;
            }
        }
    }

    public String getApiVersion(){
        return "1.6";
    }

}
