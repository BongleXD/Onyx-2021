package cn.newcraft.system.bukkit.level;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class XpTask {

    private BukkitTask task;

    public XpTask(){
        this.task = new BukkitRunnable(){
            @Override
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()){
                    PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
                    if(prof != null) {
                        prof.addXp(30);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 3 * 60 * 20, 3 * 60 * 20);
    }

    public void cancel(){
        this.task.cancel();
        this.task = null;
    }

    public boolean isCancelled(){
        return this.task == null;
    }
}
