package net.blastmc.onyx.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInitEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private String joinMessage;

    public PlayerInitEvent(Player p, String joinMessage) {
        this.p = p;
        this.joinMessage = joinMessage;
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

    public String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }

}
