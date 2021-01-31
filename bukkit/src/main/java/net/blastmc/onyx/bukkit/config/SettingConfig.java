package net.blastmc.onyx.bukkit.config;

public class SettingConfig extends ConfigManager {

    public static SettingConfig config;

    @Config(path = "setting.lynx-check")
    public static boolean LYNX = false;
    @Config(path = "setting.reg-lobby-command")
    public static boolean LOBBY_REG = true;
    @Config(path = "setting.reg-spawn-command")
    public static boolean SPAWN_REG = true;
    @Config(path = "setting.join-msg-enabled")
    public static boolean JOIN_MSG_ENABLED = true;
    @Config(path = "setting.quit-msg-enabled")
    public static boolean QUIT_MSG_ENABLED = true;
    @Config(path = "setting.join-msg")
    public static String JOIN_MSG = "§8[§a+§8] {displayname}";
    @Config(path = "setting.quit-msg")
    public static String QUIT_MSG = "§8[§c-§8] {displayname}";
    @Config(path = "setting.bungee.lobby-servers")
    public static String LOBBY = "mainLobby";
    @Config(path = "setting.bungee.lobby-server-name")
    public static String LOBBY_NAME = "主大厅";

    public SettingConfig() {
        super("setting", "plugins/Onyx");
        config = this;
    }

}
