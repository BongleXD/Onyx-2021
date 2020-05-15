package cn.newcraft.system.bukkit.config;

public class SettingConfig extends ConfigManager {

    public static SettingConfig cfg;

    public SettingConfig() {
        super("setting", "plugins/NewCraftSystem");
    }

    public static void init(){
        SettingConfig.cfg = new SettingConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("Setting.CheckLynx", false);
        cfg.getYml().addDefault("Setting.RegLobbyCommand", true);
        cfg.getYml().addDefault("Setting.JoinMessage.Enable", true);
        cfg.getYml().addDefault("Setting.JoinMessage.Text", "§8[§a+§8] {displayname}");
        cfg.getYml().addDefault("Setting.QuitMessage.Enable", true);
        cfg.getYml().addDefault("Setting.QuitMessage.Text", "§8[§c-§8] {displayname}");
        cfg.save();
    }
}
