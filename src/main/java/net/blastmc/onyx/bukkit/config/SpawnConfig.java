package net.blastmc.onyx.bukkit.config;

public class SpawnConfig extends ConfigManager{

    public static SpawnConfig cfg;

    public SpawnConfig() {
        super("spawn", "plugins/BlastMC");
    }

    public static void init(){
        SpawnConfig.cfg = new SpawnConfig();
    }
}
