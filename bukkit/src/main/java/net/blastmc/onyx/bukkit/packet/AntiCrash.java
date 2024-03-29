package net.blastmc.onyx.bukkit.packet;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.utils.ReflectUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AntiCrash {

    public AntiCrash(){
        PacketProtocol.addPacketListener(new PacketReadEvent() {
            @Override
            public void onRead(Player p, Packet packet) {
                if (packet.getName().equals("PacketPlayInCustomPayload")) {
                    try {
                        Object b = ReflectUtils.getObject(packet.getPacket(), "b");
                        int size = (int) ReflectUtils.invokeMethod(b, "readableBytes");
                        if (size > 25000) {
                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                if (p != null && p.isOnline()) {
                                    p.kickPlayer("§c与服务器断开连接！");
                                }
                            });
                            this.setCancelled(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
