package cn.newcraft.system.bukkit.util.interact;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class SoundUtil {

    private final static String VERSION = Bukkit.getVersion();

    public final static Sound LEVEL_UP = VERSION.contains("1.8") ? Sound.valueOf("LEVEL_UP") : VERSION.contains("1.12") ? Sound.valueOf("ENTITY_PLAYER_LEVELUP") : null;

    public final static Sound NOTE_PLING = VERSION.contains("1.8") ? Sound.valueOf("NOTE_PLING") : VERSION.contains("1.12") ? Sound.valueOf("BLOCK_NOTE_PLING") : null;

    public final static Sound NOTE_BASS = VERSION.contains("1.8") ? Sound.valueOf("NOTE_BASS") : VERSION.contains("1.12") ? Sound.valueOf("BLOCK_NOTE_BASS") : null;

    public final static Sound NOTE_STICKS = VERSION.contains("1.8") ? Sound.valueOf("NOTE_STICKS") : VERSION.contains("1.12") ? Sound.valueOf("NOTE_HAT") : null;

    public final static Sound ORB_PICKUP = VERSION.contains("1.8") ? Sound.valueOf("ORB_PICKUP") : VERSION.contains("1.12") ? Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP") : null;

}
