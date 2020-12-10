package net.blastmc.onyx.api.bukkit.event;

import net.blastmc.onyx.api.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDataCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private PlayerData data;

    public PlayerDataCreateEvent(PlayerData data) {
        this.data = data;
    }

    public PlayerData getData() {
        return data;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
