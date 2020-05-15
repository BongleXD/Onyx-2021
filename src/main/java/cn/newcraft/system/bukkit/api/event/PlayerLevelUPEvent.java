package cn.newcraft.system.bukkit.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLevelUPEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private int oldLevel;
    private int newLevel;

    public PlayerLevelUPEvent(Player p, int oldLevel, int newLevel) {
        this.p = p;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public Player getPlayer() {
        return p;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
