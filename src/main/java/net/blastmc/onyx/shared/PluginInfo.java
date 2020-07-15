package net.blastmc.onyx.shared;

public class PluginInfo {

    private static String ver;

    public static void init(String version){
        ver = version;
    }

    public static String getVersion() {
        return ver;
    }

    public static String getAuthor() {
        return "Hello_Han";
    }

    public static String getPlugin() {
        return "Onyx";
    }

}
