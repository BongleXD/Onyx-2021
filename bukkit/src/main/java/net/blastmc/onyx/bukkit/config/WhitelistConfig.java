package net.blastmc.onyx.bukkit.config;

import com.google.common.base.Charsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WhitelistConfig extends ConfigManager{

    public static WhitelistConfig cfg;
    @Config(path = "enabled")
    public static boolean ENABLED = false;
    @Config(path = "white-list")
    public static List<String> WHITE_LIST = new ArrayList<>(Collections.singleton("b4d89443-69d4-365d-abaa-46036c4e7eb7"));

    public WhitelistConfig() {
        super("whitelist");
        cfg = this;
    }

    public static boolean addPlayer(String name){
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        List<String> whiteList = cfg.getYml().getStringList("white-list");
        if(whiteList.contains(uuid.toString())){
            return false;
        }
        whiteList.add(uuid.toString());
        cfg.set("white-list", whiteList);
        cfg.save();
        return true;
    }

    public static void setEnabled(boolean value){
        cfg.getYml().set("enabled", value);
        cfg.save();
    }

    public static boolean removePlayer(String name){
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        List<String> whiteList = cfg.getYml().getStringList("white-list");
        if(!whiteList.contains(uuid.toString())){
            return false;
        }
        whiteList.remove(uuid.toString());
        cfg.set("white-list", whiteList);
        cfg.save();
        return true;
    }

    public static List<String> getWhitelist(){
        return cfg.getYml().getStringList("white-list");
    }

}
