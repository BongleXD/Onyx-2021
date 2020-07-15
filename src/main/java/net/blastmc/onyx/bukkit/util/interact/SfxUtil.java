package net.blastmc.onyx.bukkit.util.interact;

import org.bukkit.entity.Player;

public class SfxUtil {

    public static void playKillSound(Player p){
        p.playSound(p.getLocation(), SoundUtil.ORB_PICKUP, (float) 1, (float) 1.8);
    }

}
