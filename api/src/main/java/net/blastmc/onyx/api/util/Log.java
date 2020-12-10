package net.blastmc.onyx.api.util;

import net.blastmc.onyx.api.PluginInfo;
import net.md_5.bungee.api.ChatColor;

public class Log {

    public static Log getLogger(){
        return new Log();
    }

    public void sendLog(String message){
        sendRawMessage("§b§lOnyx-Log §7> " + message);
    }

    public void sendError(String message){
        sendRawMessage("§c§lOnyx-Error §7> " + message + " §coccured at ver " + PluginInfo.getVersion());
    }

    public void sendWarning(String message){
        sendRawMessage("§e§lOnyx-Warning §7> " + message);
    }

    public void sendRawMessage(String message){
        System.out.println(ChatColor.stripColor(message));
    }

}
