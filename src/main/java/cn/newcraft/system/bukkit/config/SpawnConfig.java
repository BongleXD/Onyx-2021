package cn.newcraft.system.bukkit.config;

import cn.newcraft.system.bukkit.Main;

public class SpawnConfig extends ConfigManager{

    public static SpawnConfig cfg;

    public SpawnConfig() {
        super("spawn", "plugins/NewCraftSystem");
    }

    public static void init(){
        SpawnConfig.cfg = new SpawnConfig();
    }
}
