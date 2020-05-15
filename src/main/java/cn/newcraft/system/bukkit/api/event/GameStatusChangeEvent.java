package cn.newcraft.system.bukkit.api.event;

import cn.newcraft.system.bukkit.gamemanager.GameManager;
import cn.newcraft.system.bukkit.gamemanager.GameStatus;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStatusChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private GameManager manager;
    private GameStatus oldStatus;
    private GameStatus newStatus;

    public GameStatusChangeEvent(GameManager manager, GameStatus oldStatus, GameStatus newStatus) {
        this.manager = manager;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public GameManager getManager() {
        return manager;
    }

    public GameStatus getOldStatus() {
        return oldStatus;
    }

    public GameStatus getNewStatus() {
        return newStatus;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
