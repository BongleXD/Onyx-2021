package cn.newcraft.system.bukkit.limit;

import cn.newcraft.system.bukkit.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandLimitListener implements Listener {

    public CommandLimitListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        Player p = e.getPlayer();
        List<String> list = Main.getInstance().getConfig().getStringList("disabled-cmd");
        if(!p.hasPermission("ncs.command.bypass")){
            for(String s : list){
                if(e.getMessage().startsWith(s)) {
                    p.sendMessage("§c此指令已在当前服务器禁用！");
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

}
