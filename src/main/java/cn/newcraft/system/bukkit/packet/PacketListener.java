package cn.newcraft.system.bukkit.packet;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.event.PacketReceiveEvent;
import cn.newcraft.system.bukkit.api.event.PacketSendEvent;
import io.netty.channel.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;

public class PacketListener implements Listener {

    public PacketListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        inject(e.getPlayer());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e){
        remove(e.getPlayer());
    }

    private void remove(Player p){
        try {
            Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
            Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            Object networkManager = connection.getClass().getField("networkManager").get(connection);
            Channel channel = (Channel) networkManager.getClass().getField("channel").get(networkManager);
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(p.getName());
                return null;
            });
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void inject(Player p){
        ChannelDuplexHandler handler = new ChannelDuplexHandler(){

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                PacketReceiveEvent event = new PacketReceiveEvent(p, ctx, packet);
                Bukkit.getPluginManager().callEvent(event);
                if(event.isCancelled()){
                    return;
                }
                super.channelRead(ctx, packet);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
                PacketSendEvent event = new PacketSendEvent(p, ctx, packet, promise);
                Bukkit.getPluginManager().callEvent(event);
                if(event.isCancelled()){
                    return;
                }
                super.write(ctx, packet, promise);
            }

        };
        try {
            Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
            Object connection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            Object networkManager = connection.getClass().getField("networkManager").get(connection);
            Channel channel = (Channel) networkManager.getClass().getField("channel").get(networkManager);
            ChannelPipeline pipeLine = channel.pipeline();
            pipeLine.addBefore("packet_handler", p.getName(), handler);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
