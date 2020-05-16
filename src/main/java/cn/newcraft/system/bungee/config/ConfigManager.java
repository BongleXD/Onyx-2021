package cn.newcraft.system.bungee.config;

import cn.newcraft.system.bungee.Main;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private File file;
    private Configuration yml;

    public ConfigManager(String name){
        File file = new File(Main.getInstance().getDataFolder() + "/" + name + ".yml");
        this.file = file;
        if(!file.getParentFile().exists()){
            file.mkdirs();
        }
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.yml = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDefault(String path, Object value){
        if(this.yml.get(path) == null){
            this.yml.set(path, value);
        }
    }

    public Configuration getYml(){
        return yml;
    }

    public void save(){
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(yml, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload(){
        try {
            this.yml = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
