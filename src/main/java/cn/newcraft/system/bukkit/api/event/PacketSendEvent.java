package cn.newcraft.system.bukkit.api.event;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketSendEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player p;
    private final ChannelHandlerContext ctx;
    private final Object packet;
    private final ChannelPromise promise;
    private boolean cancelled = false;

    public PacketSendEvent(Player p, ChannelHandlerContext ctx, Object packet, ChannelPromise promise) {
        this.p = p;
        this.ctx = ctx;
        this.packet = packet;
        this.promise = promise;
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

    public ChannelHandlerContext getChannelHandlerContext() {
        return ctx;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ChannelPromise getChannelPromise() {
        return promise;
    }

    public Player getPlayer() {
        return p;
    }

}
