package cn.newcraft.system.bukkit.api.event;

import cn.newcraft.system.shared.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ProfileCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private PlayerData data;

    public ProfileCreateEvent(PlayerData data) {
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
