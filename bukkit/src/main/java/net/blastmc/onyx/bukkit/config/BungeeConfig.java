package net.blastmc.onyx.bukkit.config;

public class BungeeConfig extends ConfigManager {

    public static BungeeConfig config;

    @Config(path = "settings.lobby-servers")
    public static String LOBBY = "mainLobby";
    @Config(path = "settings.lobby-server-name")
    public static String LOBBY_NAME = "主大厅";

    public BungeeConfig() {
        super("bungee");
        config = this;
    }

}
