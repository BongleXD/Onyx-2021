package net.blastmc.onyx.bukkit.utils.interact;

import java.lang.reflect.Field;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.utils.ReflectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class SignGUI implements Listener {

    public void open(Player p, String line1, String line2, String line3, String line4, EditCompleteListener listener) {
        String version = Main.getBukkitVer();
        Block b = p.getWorld().getBlockAt(p.getLocation().getBlockX(), 255, p.getLocation().getBlockZ());
        b.setType(Material.getMaterial((version.startsWith("v1_14") || version.startsWith("v1_15")) ? "OAK_SIGN" : (version.startsWith("v1_13") ? "SIGN" : "SIGN_POST")));
        Sign sign = (Sign) b.getState();
        sign.setLine(0, line1);
        sign.setLine(1, line2);
        sign.setLine(2, line3);
        sign.setLine(3, line4);
        sign.update();
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            try {
                boolean useCraftBlockEntityState = version.startsWith("v1_15") || version.startsWith("v1_14") || version.startsWith("v1_13") || version.startsWith("v1_12");
                Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
                Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
                Field tileField = (useCraftBlockEntityState ? ReflectUtils.getCraftClass("block.CraftBlockEntityState") : sign.getClass()).getDeclaredField(useCraftBlockEntityState ? "tileEntity" : "sign");
                tileField.setAccessible(true);
                Object tileSign = tileField.get(sign);
                Field editable = tileSign.getClass().getDeclaredField("isEditable");
                editable.setAccessible(true);
                editable.set(tileSign, true);
                Field handler = tileSign.getClass().getDeclaredField(version.startsWith("v1_15") ? "c" : (version.startsWith("v1_14") ? "j" : (version.startsWith("v1_13") ? "g" : "h")));
                handler.setAccessible(true);
                handler.set(tileSign, entityPlayer);
                playerConnection.getClass().getDeclaredMethod("sendPacket", ReflectUtils.getNMSClass("Packet")).invoke(playerConnection, ReflectUtils.getNMSClass("PacketPlayOutOpenSignEditor").getConstructor(ReflectUtils.getNMSClass("BlockPosition")).newInstance(ReflectUtils.getNMSClass("BlockPosition").getConstructor(double.class, double.class, double.class).newInstance(sign.getX(), sign.getY(), sign.getZ())));
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> b.setType(Material.AIR), 5);
                Object networkManager = playerConnection.getClass().getDeclaredField("networkManager").get(playerConnection);
                Channel channel = (Channel) networkManager.getClass().getDeclaredField("channel").get(networkManager);
                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onQuit(PlayerQuitEvent e) {
                        if(e.getPlayer() == p) {
                            if (channel.pipeline().get("PacketInjector") != null)
                                channel.pipeline().remove("PacketInjector");
                        }
                    }
                    @EventHandler
                    public void onKick(PlayerKickEvent e) {
                        if(e.getPlayer() == p) {
                            if (channel.pipeline().get("PacketInjector") != null)
                                channel.pipeline().remove("PacketInjector");
                        }
                    }
                }, Main.getInstance());
                if (channel.pipeline().get("PacketInjector") == null) {
                    channel.pipeline().addBefore("packet_handler", "PacketInjector", new ChannelDuplexHandler() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                            if(packet.getClass().getName().endsWith("PacketPlayInUpdateSign")) {
                                Object[] rawLines = (Object[]) ReflectUtils.getField(packet.getClass(), "b").get(packet);
                                Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String[] lines = new String[4];
                                            if(version.startsWith("v1_8")) {
                                                int i = 0;
                                                for (Object obj : rawLines) {
                                                    lines[i] = (String) obj.getClass().getMethod("getText").invoke(obj);

                                                    i++;
                                                }
                                            } else
                                                lines = (String[]) rawLines;
                                            if (channel.pipeline().get("PacketInjector") != null)
                                                channel.pipeline().remove("PacketInjector");
                                            listener.onEditComplete(new EditCompleteEvent(lines));
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }

                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 5);
    }

    public interface EditCompleteListener {

        void onEditComplete(EditCompleteEvent e);

    }

    public class EditCompleteEvent {

        private String[] lines;

        public EditCompleteEvent(String[] lines) {
            this.lines = lines;
        }

        public String[] getLines() {
            return lines;
        }

    }

}
