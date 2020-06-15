package cn.newcraft.system.bukkit.api;

import cn.newcraft.system.bukkit.proxy.ServerType;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.event.PlayerDataCreateEvent;
import cn.newcraft.system.bukkit.config.TagConfig;
import cn.newcraft.system.bukkit.support.NMS;
import cn.newcraft.system.bukkit.util.TeamAction;
import cn.newcraft.system.shared.util.SQLHelper;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Random;
import java.util.UUID;

import static cn.newcraft.system.bukkit.util.Method.getTagData;
import static cn.newcraft.system.bukkit.util.Method.vanishPlayer;

public class SystemAPI {

    private final static SystemAPI api = new SystemAPI();

    public static SystemAPI getApi() {
        return api;
    }

    public static SQLHelper getSQL(){
        return Main.getSQL();
    }

    public String getPID(UUID uuid){
        return (String) getSQL().getData("uuid", uuid.toString(), "player_data", "pid").get(0);
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
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, Method.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, other));
                //restore tag
                String suffix = PlaceholderAPI.setPlaceholders(p, getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                String priority = Method.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
                if (p != online) {
                    String otherSuffix = PlaceholderAPI.setPlaceholders(online, getTagData(online).getSuffix());
                    if (other.isVanish()) {
                        otherSuffix = " §c[已隐身]";
                    }
                    Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, otherSuffix), TeamAction.CREATE, Method.getTagPriority(online, other));
                }
            }
        }
        for (UUID uuid : PlayerProfile.getVanishs()) {
            PlayerProfile profile = PlayerProfile.getDataFromUUID(uuid);
            if (profile.isVanish()) {
                vanishPlayer(Bukkit.getPlayer(uuid), p, true);
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
                String priority = Method.getTagPriority(online, prof);
                String suffix = PlaceholderAPI.setPlaceholders(online, getTagData(online).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, priority);
                Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, getTagData(online).getPrefix()), suffix, TeamAction.CREATE, priority);
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
                uuid.toString(),
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
                uuid.toString(),
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
                sb.append("NC");
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
