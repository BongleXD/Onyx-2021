package cn.newcraft.system.bukkit.config;

public class BungeeConfig extends ConfigManager {

    public static BungeeConfig cfg;

    public BungeeConfig() {
        super("bungee", "plugins/NewCraftSystem");
    }

    public static void init(){
        BungeeConfig.cfg = new BungeeConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("BungeeCord.Enable", true);
        cfg.getYml().addDefault("BungeeCord.LobbyServer", "Main_Lobby");
        cfg.getYml().addDefault("BungeeCord.ServerName", "主大厅");
        cfg.save();
    }
}
