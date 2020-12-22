package net.blastmc.onyx.bukkit.config;

import org.bukkit.Location;

public class SpawnConfig extends ConfigManager{

    public static SpawnConfig config;
    @Config(path = "spawn.loc")
    public static Location SPAWN_LOC = null;

    public SpawnConfig() {
        super("spawn");
        config = this;
    }

}
