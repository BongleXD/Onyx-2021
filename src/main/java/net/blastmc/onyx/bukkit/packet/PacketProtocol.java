package net.blastmc.onyx.bukkit.packet;

import net.blastmc.onyx.bukkit.Main;
import io.netty.channel.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PacketProtocol implements Listener {

    private static List<PacketReadEvent> readListeners = new ArrayList<>();
    private static List<PacketSendEvent> writeListeners = new ArrayList<>();

    public static void addPacketListener(PacketListener listener){
        if(listener instanceof PacketReadEvent){
            readListeners.add((PacketReadEvent) listener);
        }else if(listener instanceof PacketSendEvent){
            writeListeners.add((PacketSendEvent) listener);
        }
    }

    public static void removePacketListener(PacketListener listener){
        readListeners.remove(listener);
        writeListeners.remove(listener);
    }

    public PacketProtocol(){
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

    private void remove(Player p) {
        Channel channel = Main.getNMS().getChannel(p);
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(p.getName());
            return null;
        });
    }

    private void inject(Player p){
        ChannelDuplexHandler handler = new ChannelDuplexHandler(){

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                boolean cancelled = false;
                for(PacketReadEvent e : readListeners){
                    e.setCancelled(cancelled);
                    e.onRead(p, new Packet(packet));
                    cancelled = e.isCancelled();
                }
                if(cancelled){
                    return;
                }
                super.channelRead(ctx, packet);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
                boolean cancelled = false;
                for(PacketSendEvent e : writeListeners){
                    e.setCancelled(cancelled);
                    e.onWrite(p, new Packet(packet));
                    cancelled = e.isCancelled();
                }
                if(cancelled){
                    return;
                }
                super.write(ctx, packet, promise);
            }

        };
        ChannelPipeline pipeLine = Main.getNMS().getChannel(p).pipeline();
        pipeLine.addBefore("packet_handler", p.getName(), handler);
    }
}
