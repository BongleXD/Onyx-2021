package net.blastmc.onyx.bukkit.config;

import com.google.common.base.Charsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WhitelistConfig extends ConfigManager{

    public static WhitelistConfig config;
    @Config(path = "enabled")
    public static boolean ENABLED = false;
    @Config(path = "white-list")
    public static List<String> WHITE_LIST = new ArrayList<>(Collections.singleton("b4d89443-69d4-365d-abaa-46036c4e7eb7"));

    public WhitelistConfig() {
        super("whitelist");
        config = this;
    }

    public static boolean addPlayer(String name){
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        if(WHITE_LIST.contains(uuid.toString())){
            return false;
        }
        WHITE_LIST.add(uuid.toString());
        config.save();
        return true;
    }

    public static void setEnabled(boolean value){
        ENABLED = value;
        config.save();
    }

    public static boolean removePlayer(String name){
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));
        if(!WHITE_LIST.contains(uuid.toString())){
            return false;
        }
        WHITE_LIST.remove(uuid.toString());
        config.save();
        return true;
    }

}
