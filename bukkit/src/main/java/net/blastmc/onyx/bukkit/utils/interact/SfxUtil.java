package net.blastmc.onyx.bukkit.utils.interact;

import org.bukkit.entity.Player;

public class SfxUtil {

    public static void playKillSound(Player p){
        p.playSound(p.getLocation(), SoundUtil.ORB_PICKUP.getSound(), (float) 1, (float) 1.8);
    }

}
