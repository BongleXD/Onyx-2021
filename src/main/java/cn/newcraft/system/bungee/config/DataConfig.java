package cn.newcraft.system.bungee.config;

import java.util.List;

public class DataConfig extends ConfigManager {

    public static DataConfig cfg;

    public DataConfig() {
        super("data");
    }

    public static void init(){
        DataConfig.cfg = new DataConfig();
        cfg.getYml().getStringList("Data.BlackIP");
        cfg.getYml().set("Data.BlackIP", cfg.getYml().getStringList("Data.BlackIP").isEmpty() ? "" : cfg.getYml().getStringList("Data.BlackIP"));
        cfg.save();
    }

    public void addIP(String ip){
        List<String> blackIP = this.getYml().getStringList("Data.BlackIP");
        if(!blackIP.contains(ip)) {
            blackIP.add(ip);
        }
        this.getYml().set("Data.BlackIP", blackIP);
        this.save();
    }

    public void removeIP(String ip){
        List<String> blackIP = this.getYml().getStringList("Data.BlackIP");
        blackIP.remove(ip);
        this.getYml().set("Data.BlackIP", blackIP);
        this.save();
    }

    public void addWhitePlayers(String player){
        List<String> whitePlayers = this.getYml().getStringList("Data.WhitePlayers");
        if(!whitePlayers.contains(player)) {
            whitePlayers.add(player);
        }
        this.getYml().set("Data.WhitePlayers", whitePlayers);
        this.save();
    }

    public void removeWhitePlayers(String player){
        List<String> whitePlayers = this.getYml().getStringList("Data.WhitePlayers");
        whitePlayers.remove(player);
        this.getYml().set("Data.WhitePlayers", whitePlayers);
        this.save();
    }
}
