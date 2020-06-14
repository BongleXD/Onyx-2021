package cn.newcraft.system.bukkit.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInitEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player p;

    public PlayerInitEvent(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
