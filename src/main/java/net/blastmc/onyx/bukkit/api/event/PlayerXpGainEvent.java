package net.blastmc.onyx.bukkit.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerXpGainEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private int amount;
    private double boost;

    public PlayerXpGainEvent(Player p, int amount, double boost) {
        this.p = p;
        this.amount = amount;
        this.boost = boost;
    }

    public Player getPlayer() {
        return p;
    }

    public int getAmount() {
        return amount;
    }

    public double getBoost() {
        return boost;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
