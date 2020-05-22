package cn.newcraft.system.bukkit.config;

public class SettingConfig extends ConfigManager {

    public static SettingConfig cfg;

    public SettingConfig() {
        super("setting", "plugins/NewCraftSystem");
    }

    public static void init(){
        SettingConfig.cfg = new SettingConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("setting.lynx-check", false);
        cfg.getYml().addDefault("setting.reg-lobby-command", true);
        cfg.getYml().addDefault("setting.join-msg-enabled", true);
        cfg.getYml().addDefault("setting.join-msg", "§8[§a+§8] {displayname}");
        cfg.getYml().addDefault("setting.quit-msg-enabled", true);
        cfg.getYml().addDefault("setting.quit-msg", "§8[§c-§8] {displayname}");
        cfg.save();
    }
}
