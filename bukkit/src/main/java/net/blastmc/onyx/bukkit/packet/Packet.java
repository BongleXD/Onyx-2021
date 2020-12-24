package net.blastmc.onyx.bukkit.packet;

public class Packet {

    private Object packet;
    private String name;

    public Packet(Object packet) {
        this.packet = packet;
        this.name = packet.getClass().getSimpleName();
    }

    public Object getPacket() {
        return packet;
    }

    public void setPacket(Object packet) {
        this.packet = packet;
    }

    public String getName() {
        return name;
    }

}
