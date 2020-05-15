package cn.newcraft.system.bukkit.util.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TitleEvent extends Event {
	
    private static HandlerList handlers = new HandlerList();
    private Player player;
    private String title;
    private String subtitle;
    private boolean cancelled = false;

    public TitleEvent(Player p, String title, String subtitle) {
        this.player = p;
        this.title = title;
        this.subtitle = subtitle;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
