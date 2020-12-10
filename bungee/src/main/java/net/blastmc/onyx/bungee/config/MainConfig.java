package net.blastmc.onyx.bungee.config;

public class MainConfig extends ConfigManager{

    public static MainConfig cfg;

    public MainConfig() {
        super("config");
    }

    public static void init(){
        MainConfig.cfg = new MainConfig();
        cfg.addDefault("url", "localhost:3306");
        cfg.addDefault("user", "root");
        cfg.addDefault("passwd", "passwd");
        cfg.addDefault("database", "database");
        cfg.save();
    }

}
