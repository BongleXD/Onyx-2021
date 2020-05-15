package cn.newcraft.system.bukkit.config;

import cn.newcraft.system.bukkit.util.Method;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private YamlConfiguration yml;
    private File config;
    private String name;

    public ConfigManager(String name, String folder) {
        this.config = new File(folder + "/" + name + ".yml");
        try {
            this.config.createNewFile();
            Bukkit.getConsoleSender().sendMessage("§a正在创建 " + folder + "/" + name + ".yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.yml = YamlConfiguration.loadConfiguration(this.config);
        this.name = name;
    }

    public void reload(){
        this.yml = YamlConfiguration.loadConfiguration(this.config);
    }

    public void set(String string, Object object) {
        this.yml.set(string, object);
        this.save();
    }

    public void save() {
        try {
            this.yml.save(this.config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getYml(){
        return this.yml;
    }

    public String getName() {
        return this.name;
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

}
