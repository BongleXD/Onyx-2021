package net.blastmc.onyx.bukkit.config;

public class RankConfig extends ConfigManager {

    public static RankConfig cfg;

    public RankConfig() {
        super("rank", "plugins/BlastMC");
    }

    public static void init(){
        RankConfig.cfg = new RankConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("rank.default.displayname", "&7默认");
        cfg.getYml().addDefault("rank.default.perm", "");
        cfg.getYml().addDefault("rank.default.color", "&7");
        cfg.getYml().addDefault("rank.default.priority", 9);
        cfg.save();
    }

}
