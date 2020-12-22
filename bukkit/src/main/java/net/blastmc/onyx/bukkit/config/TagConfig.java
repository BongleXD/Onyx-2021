package net.blastmc.onyx.bukkit.config;

import java.util.Arrays;
import java.util.List;

public class TagConfig extends ConfigManager {

    public static TagConfig config;
    @Config(path = "enabled")
    public static boolean ENABLED = true;
    @Config(path = "mysql")
    public static boolean MYSQL_ENABLED = true;
    @Config(path = "enabled-world")
    public static List<String> ENABLED_WORLD = Arrays.asList("world", "world_nether", "world_the_end");

    public TagConfig() {
        super("tag");
        config = this;
        this.getYml().options().copyDefaults(true);
        this.getYml().addDefault("group.default.prefix", "%profile_prefix%");
        this.getYml().addDefault("group.default.suffix", "%profile_suffix%");
        this.getYml().addDefault("group.default.perm", "");
        this.getYml().addDefault("group.default.priority", 0);
        this.save();
    }

}
