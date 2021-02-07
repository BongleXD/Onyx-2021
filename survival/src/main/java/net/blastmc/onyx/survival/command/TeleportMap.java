package net.blastmc.onyx.survival.command;

import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportMap {

    private static final Map<UUID, BukkitTask> tpQueue = new HashMap<>();

    public static Map<UUID, BukkitTask> getTpQueue() {
        return tpQueue;
    }

}
