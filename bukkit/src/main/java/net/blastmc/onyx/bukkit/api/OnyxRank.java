package net.blastmc.onyx.bukkit.api;

import net.blastmc.onyx.api.bukkit.Rank;
import net.blastmc.onyx.bukkit.config.RankConfig;

import java.util.HashMap;
import java.util.Set;

public class OnyxRank implements Rank {

    private String name;
    private String displayName;
    private String perm;
    private String color;
    private int priority;
    private static HashMap<String, OnyxRank> dataMap = new HashMap<>();

    public OnyxRank(String name, String displayName, String perm, String color, int priority) {
        this.name = name;
        this.displayName = displayName;
        this.perm = perm;
        this.color = color;
        this.priority = priority;
        dataMap.put(name, this);
    }

    public static void init(){
        for (String rank : RankConfig.cfg.getYml().getConfigurationSection("rank").getKeys(false)) {
            if (dataMap.containsKey(rank)) {
                continue;
            }
            new OnyxRank(rank, RankConfig.cfg.getString("rank." + rank + ".displayname"), RankConfig.cfg.getString("rank." + rank + ".perm"), RankConfig.cfg.getString("rank." + rank + ".color"), RankConfig.cfg.getInt("rank." + rank + ".priority"));
        }
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPerm() {
        return perm;
    }

    public String getColor() {
        return color;
    }

    public int getPriority() {
        return priority;
    }

    public static OnyxRank getData(String name){
        return dataMap.getOrDefault(name, null);
    }

    public static Set<String> getRanks(){
        return dataMap.keySet();
    }

}
