package net.blastmc.onyx.api;

import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.Rank;
import net.blastmc.onyx.api.util.Log;

import java.util.HashMap;
import java.util.UUID;

public class Onyx {

    private static API api;
    static HashMap<String, String> pidMap = new HashMap<>();
    static HashMap<String, PlayerData> dataMap = new HashMap<>();

    public static API getAPI(){
        return api;
    }

    public static String getAPIVersion(){
        return api.getAPIVersion();
    }

    public static void setAPI(API api) {
        if (Onyx.api != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton API");
        }
        Onyx.api = api;
        Log.getLogger().sendLog("§aOnyx API 导入成功！ (API 版本 " + getAPIVersion() + ")");
    }

    public static PlayerProfile getPlayerProfile(UUID uuid){
        return api.getPlayerProfile(uuid);
    }

    public static PlayerProfile getPlayerProfile(String name){
        return api.getPlayerProfile(name);
    }

    public static PlayerProfile getPlayerProfileFromPID(String pid){
        return api.getPlayerProfileFromPID(pid);
    }

    public static PlayerData init(UUID uuid, String name) {
        try {
            String pid = (String) api.getSQL().getData("player_data", "uuid", uuid.toString(), "pid").get(0);
            if (pid != null) {
                pidMap.put(uuid.toString(), pid);
                pidMap.put(name, pid);
                return new PlayerData(pid);
            }else{
                pid = (String) api.getSQL().getData("player_data", "uuid", uuid.toString(), "pid").get(0);
                if (pid != null) {
                    pidMap.put(uuid.toString(), pid);
                    pidMap.put(name, pid);
                    return new PlayerData(pid);
                }
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public static PlayerData initFromUUID(UUID uuid){
        try {
            String pid = (String) api.getSQL().getData("player_data", "uuid", uuid.toString(), "pid").get(0);
            if (pid != null) {
                pidMap.put(uuid.toString(), pid);
                return new PlayerData(pid);
            }
        }catch (Exception ex){
            return null;
        }
        return null;
    }

    public static PlayerData getPlayerData(UUID uuid){
        return dataMap.getOrDefault(pidMap.get(uuid.toString()), null);
    }

    public static PlayerData getPlayerData(String name){
        return dataMap.getOrDefault(pidMap.get(name), null);
    }

    public static PlayerData getPlayerDataFromPID(String pid){
        return dataMap.getOrDefault(pid, null);
    }

    public static PlayerData initFromName(String name) {
        try {
            String pid = (String) api.getSQL().getData("player_data", "name", name, "pid").get(0);
            if (pid != null) {
                pidMap.put(name, pid);
                return new PlayerData(pid);
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public static void addPID(String data, String pid){
        pidMap.put(data, pid);
    }

    public static Rank getRank(String name){
        return api.getRank(name);
    }

}
