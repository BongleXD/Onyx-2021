package cn.newcraft.system.bukkit.exception;

import cn.newcraft.system.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.github.paperspigot.event.ServerExceptionEvent;

public class ExceptionListener implements Listener {

    public ExceptionListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onException(ServerExceptionEvent e){
    }
}
