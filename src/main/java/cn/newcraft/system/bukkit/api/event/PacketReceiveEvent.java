package cn.newcraft.system.bukkit.api.event;

import io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketReceiveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player p;
    private final ChannelHandlerContext ctx;
    private final Object packet;
    private boolean cancelled = false;

    public PacketReceiveEvent(Player p, ChannelHandlerContext ctx, Object packet) {
        this.p = p;
        this.ctx = ctx;
        this.packet = packet;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Object getPacket() {
        return packet;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getPlayer() {
        return p;
    }

}
