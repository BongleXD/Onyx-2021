package net.blastmc.onyx.bukkit.config;

public class ChatConfig extends ConfigManager{

    public static ChatConfig config;

    @Config(path = "enabled")
    public static boolean ENABLED = false;
    @Config(path = "format")
    public static String FORMAT = "%profile_prefix%%player_name%%profile_suffix%&f: ";
    @Config(path = "hover-enabled")
    public static boolean HOVER_ENABLED = false;
    @Config(path = "hover-format")
    public static String HOVER_FORMAT = "%player_displayname%\n§7BlastMC 等级: §b%profile_level%\n§7所在公会: §6%profile_guild%\n\n§e点击查看 %profile_color%%player_name% §e的资料";
    @Config(path = "click-enabled")
    public static boolean CLICK_ENABLED = false;
    @Config(path = "click-command")
    public static String CLICK_COMMAND = "/profile {player}";

    public ChatConfig() {
        super("chat");
        config = this;
    }

}
