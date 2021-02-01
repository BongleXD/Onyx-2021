package net.blastmc.onyx.api.utils;

import net.blastmc.onyx.api.PluginInfo;
import net.md_5.bungee.api.ChatColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

    private static final Logger logger = LoggerFactory.getLogger("Onyx");

    public static Log getLogger(){
        return new Log();
    }

    /**
     * @param message
     */
    public void sendLog(String message){
        logger.info(ChatColor.stripColor("§7> " + message));
    }

    /**
     * @param message
     */
    public void sendError(String message){
        logger.error(ChatColor.stripColor("§7> " + message + " §coccured at ver " + PluginInfo.getVersion()));
    }

    /**
     * @param message
     */
    public void sendWarning(String message){
        logger.warn(ChatColor.stripColor("§7> " + message));
    }

    /**
     * @param message
     */
    public void sendRawMessage(String message){
        System.out.println(ChatColor.stripColor(message));
    }
}
