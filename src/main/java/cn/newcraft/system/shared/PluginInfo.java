package cn.newcraft.system.shared;

import org.bukkit.plugin.PluginDescriptionFile;

public class PluginInfo {

    private static String ver;

    public static void init(String version){
        ver = version;
    }

    public static String INFO = "§bNewCraftSystem §7>";
    public static String ERROR = "§bNewCraftSystem §cERROR §7>";
    public static String WARNING = "§bNewCraftSystem §eWARNING §7>";

    public static String BUNGEE_INFO = "§bNewCraftSystem-Bungee §7>";
    public static String BUNGEE_ERROR = "§bNewCraftSystem-Bungee §cERROR §7>";
    public static String BUNGEE_WARNING = "§bNewCraftSystem-Bungee §eWARNING §7>";

    public static String getVersion() {
        return ver;
    }
    public static String getAuthor() {
        return "Hello_Han, May_Block";
    }

    public static String getPlugin() {
        return "NewCraftSystem";
    }

    public static String getBungee() {
        return "NewCraftSystem-Bungee";
    }

}
