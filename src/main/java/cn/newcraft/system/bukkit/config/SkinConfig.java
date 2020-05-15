package cn.newcraft.system.bukkit.config;

public class SkinConfig extends ConfigManager{

    public static SkinConfig cfg;

    public SkinConfig() {
        super("skins", "plugins/NewCraftSystem");
    }

    public static void init(){
        SkinConfig.cfg = new SkinConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("skin", new String[]{"Hello_Han", "May_Block"});
        cfg.save();
    }

}
