package cn.newcraft.system.bukkit.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class LanguageConfig extends ConfigManager{

    public static LanguageConfig cfg;
    public static HashMap<String, String> words;

    public LanguageConfig() {
        super("language", "plugins/NewCraftSystem");
    }

    public static void init(){
        LanguageConfig.cfg = new LanguageConfig();
        YamlConfiguration yml = cfg.getYml();
        yml.getConfigurationSection("").getKeys(false).forEach(trans -> {
            try {
                words.put(trans, yml.getString(trans));
            }catch (Exception ignored){}
        });
    }
}
