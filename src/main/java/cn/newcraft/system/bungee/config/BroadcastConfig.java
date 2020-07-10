package cn.newcraft.system.bungee.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class BroadcastConfig extends ConfigManager {

    public static long duration;
    public static HashMap<String, List<String>> messageMap = new HashMap<>();
    public static BroadcastConfig cfg;

    public BroadcastConfig() {
        super("broadcast");
    }

    public static void init(){
        BroadcastConfig.cfg = new BroadcastConfig();
        cfg.addDefault("enabled", true);
        cfg.addDefault("duration", 1000L);
        List<String> list = new ArrayList<>();;
        list.add("§c[公告] §e测试！");
        cfg.addDefault("messages.default", list);
        cfg.save();
        duration = cfg.getYml().getLong("duration");
        cfg.getYml().getSection("messages").getKeys().forEach(key -> {
            messageMap.put(key, cfg.getYml().getStringList("messages." + key));
        });
    }

    @Override
    public void reload() {
        super.reload();
        messageMap = new HashMap<>();
        duration = cfg.getYml().getLong("duration");
        cfg.getYml().getSection("messages").getKeys().forEach(key -> {
            messageMap.put(key, cfg.getYml().getStringList("messages." + key));
        });
    }
}
