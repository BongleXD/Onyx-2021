package cn.newcraft.system.bukkit.api.event;

import cn.newcraft.system.bukkit.gamemanager.GameManager;
import cn.newcraft.system.bukkit.gamemanager.ServerStatus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerStatusChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private GameManager manager;
    private ServerStatus oldStatus;
    private ServerStatus newStatus;

    public ServerStatusChangeEvent(GameManager manager, ServerStatus oldStatus, ServerStatus newStatus) {
        this.manager = manager;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public GameManager getManager() {
        return manager;
    }

    public ServerStatus getOldStatus() {
        return oldStatus;
    }

    public ServerStatus getNewStatus() {
        return newStatus;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
