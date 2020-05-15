package cn.newcraft.system.bukkit.rank;

import cn.newcraft.system.bukkit.config.RankConfig;

import java.util.HashMap;
import java.util.Set;

public class RankData {

    private String name;
    private String displayName;
    private String perm;
    private String color;
    private int priority;
    private static HashMap<String, RankData> dataMap = new HashMap<>();

    public RankData(String name, String displayName, String perm, String color, int priority) {
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
            new RankData(rank, RankConfig.cfg.getString("rank." + rank + ".displayname"), RankConfig.cfg.getString("rank." + rank + ".perm"), RankConfig.cfg.getString("rank." + rank + ".color"), RankConfig.cfg.getInt("rank." + rank + ".priority"));
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

    public static RankData getData(String name){
        return dataMap.getOrDefault(name, null);
    }

    public static Set<String> getRanks(){
        return dataMap.keySet();
    }

}
