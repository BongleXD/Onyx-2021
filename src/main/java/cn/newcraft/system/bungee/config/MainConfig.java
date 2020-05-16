package cn.newcraft.system.bungee.config;

public class MainConfig extends ConfigManager{

    public static MainConfig cfg;

    public MainConfig() {
        super("config");
    }

    public static void init(){
        MainConfig.cfg = new MainConfig();
    }

}
