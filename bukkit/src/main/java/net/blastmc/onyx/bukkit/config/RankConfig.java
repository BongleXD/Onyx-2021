package net.blastmc.onyx.bukkit.config;

public class RankConfig extends ConfigManager {

    public static RankConfig config;

    public RankConfig() {
        super("rank");
        config = this;
        this.getYml().options().copyDefaults(true);
        this.getYml().addDefault("rank.default.displayname", "&7默认");
        this.getYml().addDefault("rank.default.perm", "");
        this.getYml().addDefault("rank.default.color", "&7");
        this.getYml().addDefault("rank.default.priority", 9);
        this.save();
    }

}
