package net.blastmc.onyx.bukkit.config;

public class ChatConfig extends ConfigManager{

    public static ChatConfig cfg;

    public ChatConfig() {
        super("chat", "plugins/Onyx");
    }

    public static void init(){
        ChatConfig.cfg = new ChatConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("enabled", false);
        cfg.getYml().addDefault("format", "%profile_prefix%%player_name%%profile_suffix%&f: ");
        cfg.getYml().addDefault("hover-enabled", false);
        cfg.getYml().addDefault("hover-format", "%player_displayname%\n§7BlastMC 等级: §b%profile_level%\n§7所在公会: §6%profile_guild%\n\n§e点击查看 %profile_color%%player_name% §e的资料");
        cfg.getYml().addDefault("click-enabled", false);
        cfg.getYml().addDefault("click-command", "/profile {player}");
        cfg.save();
    }

}
