package net.blastmc.onyx.bukkit.config;

public class TagConfig extends ConfigManager {

    public static TagConfig cfg;

    public TagConfig() {
        super("tag", "plugins/Onyx");
    }

    public static void init(){
        TagConfig.cfg = new TagConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("enabled", true);
        cfg.getYml().addDefault("mysql", false);
        cfg.getYml().addDefault("enabled-world", new String[]{"world", "world_nether", "world_end"});
        cfg.getYml().addDefault("group.default.prefix", "%profile_prefix%");
        cfg.getYml().addDefault("group.default.suffix", "%profile_suffix%");
        cfg.getYml().addDefault("group.default.perm", "");
        cfg.getYml().addDefault("group.default.priority", 0);
        cfg.save();
    }

}
