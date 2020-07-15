package net.blastmc.onyx.bukkit.level;

import java.util.TreeMap;

public enum  BoostReward {
    
    XP,
    COIN;
    
    public TreeMap <Integer, Double> getBoost(){
        TreeMap <Integer, Double> map = new TreeMap<>();
        switch (this){
            case XP:
                map.put(10, 1.5);
                map.put(20, 2.0);
                map.put(30, 2.5);
                map.put(40, 3.0);
                map.put(50, 3.5);
                map.put(60, 4.0);
                map.put(70, 4.5);
                map.put(90, 5.0);
                map.put(100, 5.5);
                map.put(125, 6.0);
                map.put(145, 7.0);
                map.put(155, 7.5);
                map.put(175, 8.0);
                map.put(195, 8.5);
                map.put(215, 9.0);
                map.put(250, 9.5);
                map.put(300, 10.0);
                break;
            case COIN:
                map.put(85, 1.2);
                map.put(150, 1.4);
                map.put(175, 1.6);
                map.put(200, 1.8);
                map.put(225, 2.0);
                map.put(250, 2.1);
                map.put(300, 2.2);
                map.put(400, 2.3);
                map.put(450, 2.4);
                map.put(550, 2.5);
                break;
        }
        return map;
    }
    
}
