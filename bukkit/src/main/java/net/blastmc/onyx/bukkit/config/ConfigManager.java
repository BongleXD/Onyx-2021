package net.blastmc.onyx.bukkit.config;

import net.blastmc.onyx.api.util.Method;
import net.blastmc.onyx.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * @author: Hello_Han
 * @createDate: 2019/07/22
 * @version: 2.2
 */

public class ConfigManager {

    private YamlConfiguration yml;
    private File config;

    public ConfigManager(String name) {
        String path = Main.getInstance().getDataFolder().toString().replace("\\", "/");
        this.config = new File(path + "/" + name + ".yml");
        try {
            this.config.createNewFile();
            Bukkit.getConsoleSender().sendMessage("§a正在实例化 " + path + "/" + name + ".yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.yml = YamlConfiguration.loadConfiguration(this.config);
        init();
    }

    public ConfigManager(String name, String path) {
        this.config = new File(path + "/" + name + ".yml");
        try {
            this.config.createNewFile();
            Bukkit.getConsoleSender().sendMessage("§a正在实例化 " + path + "/" + name + ".yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.yml = YamlConfiguration.loadConfiguration(this.config);
        init();
    }

    private void init() {
        yml.options().copyDefaults(true);
        for (Field field : this.getClass().getFields()){
            Config cfg = field.getAnnotation(Config.class);
            if(cfg == null){
                continue;
            }
            try {
                Object obj = getYml().get(cfg.path(), field.get(this));
                if(obj instanceof String){
                    obj = Method.transColor((String) obj);
                }
                field.set(this, obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        try {
            this.yml.save(this.config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload(){
        this.yml = YamlConfiguration.loadConfiguration(this.config);
        init();
    }

    public void set(String string, Object object) {
        this.yml.set(string, object);
        this.save();
    }

    public void save() {
        for (Field field : this.getClass().getFields()){
            Config cfg = field.getAnnotation(Config.class);
            if(cfg == null){
                continue;
            }
            try {
                yml.set(cfg.path(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        try {
            this.yml.save(this.config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getYml(){
        return this.yml;
    }

    public boolean getBoolean(String b) {
        return this.yml.getBoolean(b);
    }

    public int getInt(String i) {
        return this.yml.getInt(i);
    }

    public String getString(String s) {
        return Method.transColor(this.yml.getString(s));
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Config{

        String path();

    }

}
