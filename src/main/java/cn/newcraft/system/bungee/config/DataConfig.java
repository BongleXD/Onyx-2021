package cn.newcraft.system.bungee.config;

import java.util.List;

public class DataConfig extends ConfigManager {

    public static DataConfig cfg;

    public DataConfig() {
        super("data");
    }

    public static void init(){
        DataConfig.cfg = new DataConfig();
        cfg.addDefault("black-ip", new String[]{});
        cfg.save();
    }

    public void addIP(String ip){
        List<String> blackIP = this.getYml().getStringList("black-ip");
        if(!blackIP.contains(ip)) {
            blackIP.add(ip);
        }
        this.getYml().set("black-ip", blackIP);
        this.save();
    }

    public void removeIP(String ip){
        List<String> blackIP = this.getYml().getStringList("black-ip");
        blackIP.remove(ip);
        this.getYml().set("black-ip", blackIP);
        this.save();
    }

    public void addWhitePlayers(String player){
        List<String> whitePlayers = this.getYml().getStringList("white-names");
        if(!whitePlayers.contains(player)) {
            whitePlayers.add(player);
        }
        this.getYml().set("white-names", whitePlayers);
        this.save();
    }

    public void removeWhitePlayers(String player){
        List<String> whitePlayers = this.getYml().getStringList("white-names");
        whitePlayers.remove(player);
        this.getYml().set("white-names", whitePlayers);
        this.save();
    }

}
