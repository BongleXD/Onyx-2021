package net.blastmc.onyx.bukkit.config;

public class BungeeConfig extends ConfigManager {

    public static BungeeConfig cfg;

    public BungeeConfig() {
        super("bungee", "plugins/Onyx");
    }

    public static void init(){
        BungeeConfig.cfg = new BungeeConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("settings.lobby-servers", "mainLobby");
        cfg.getYml().addDefault("settings.lobby-server-name", "主大厅");
        cfg.save();
    }
}
