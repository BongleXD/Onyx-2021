package net.blastmc.onyx.bukkit.leaderboard;

import java.util.HashMap;

public class Page {

    private String name;
    private HashMap<String, Double> dataMap;

    public Page(String name, HashMap<String, Double> dataMap){
        this.name = name;
        this.dataMap = dataMap;
    }

    public HashMap<String, Double> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<String, Double> dataMap) {
        this.dataMap = dataMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
