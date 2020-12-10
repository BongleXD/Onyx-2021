package net.blastmc.onyx.bukkit.util.interact;

import java.util.HashMap;
import java.util.UUID;

import net.blastmc.onyx.bukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarUtil {

    private static HashMap<UUID, BukkitTask> taskMap = new HashMap<>();

    public static void sendBar(Player p, String message, final int duration){
        taskMap.put(p.getUniqueId(), new BukkitRunnable(){
            int i = duration;
            @Override
            public void run() {
                if(i == 0)
                    this.cancel();
                else
                    Main.getNMS().sendActionBar(p, message);
                    i--;
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L));
    }

    public static void cancel(Player p){
        try {
            if(taskMap.containsKey(p.getUniqueId())){
                taskMap.get(p.getUniqueId()).cancel();
                taskMap.remove(p.getUniqueId());
            }
        }catch (NullPointerException ignored){ }
    }

}