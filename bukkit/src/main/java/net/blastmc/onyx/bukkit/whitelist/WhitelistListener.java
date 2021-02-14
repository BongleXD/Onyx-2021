package net.blastmc.onyx.bukkit.whitelist;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.config.WhitelistConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class WhitelistListener implements Listener {

    public WhitelistListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e){
        UUID uuid = e.getUniqueId();
        if(WhitelistConfig.ENABLED && !WhitelistConfig.WHITE_LIST.contains(uuid.toString())){
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "§c你未拥有此服务器的白名单！");
        }
    }
}
