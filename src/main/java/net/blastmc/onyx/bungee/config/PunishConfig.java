package net.blastmc.onyx.bungee.config;

import java.util.List;

public class PunishConfig extends ConfigManager {

    public static PunishConfig cfg;

    public PunishConfig() {
        super("punish");
    }

    public static void init(){
        PunishConfig.cfg = new PunishConfig();
        cfg.addDefault("ban.broadcast-message", "§c§l封禁！ §f一名玩家因使用第三方软件造成的 §e不平等优势 §f或其他原因被移除了此服务器！");
        cfg.addDefault("mute.black-list", new String[]{
                "/me",
                "/say",
                "/tell",
                "/whisper",
                "/reply",
                "/pm",
                "/message",
                "/msg",
                "/emsg",
                "/etell",
                "/ewhisper",
                "/w",
                "/m",
                "/t",
                "/r",
                "/mail send"
        });
        cfg.save();
    }

    public List<String> getBlackList(){
        return cfg.getYml().getStringList("mute.black-list");
    }

}
