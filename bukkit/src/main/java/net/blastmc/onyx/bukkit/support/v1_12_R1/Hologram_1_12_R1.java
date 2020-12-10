package net.blastmc.onyx.bukkit.support.v1_12_R1;

import net.blastmc.onyx.bukkit.exception.HologramException;
import net.blastmc.onyx.bukkit.support.Hologram;
import com.google.common.collect.Lists;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Hologram_1_12_R1 implements Hologram {

    private Location loc;
    private List<UUID> players;
    private List<UUID> create;
    private List<EntityArmorStand> lines;
    private double offset = 0.23D;
    private boolean show = false;

    public Hologram_1_12_R1(Location loc, String... lines){
        this.loc = loc;
        this.lines = Lists.newArrayList();
        this.players = Lists.newArrayList();
        this.create = Lists.newArrayList();
        for (String line : lines) {
            EntityArmorStand stand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle());
            stand.setNoGravity(true);
            stand.setInvisible(true);
            stand.setSmall(true);
            stand.setCustomNameVisible(true);
            stand.setCustomName(line);
            this.lines.add(stand);
        }
    }

    public Hologram_1_12_R1(Location loc){
        this.loc = loc;
        this.lines = Lists.newArrayList();
        this.players = Lists.newArrayList();
        this.create = Lists.newArrayList();
    }

    @Override
    public double getOffset() {
        return this.offset;
    }

    @Override
    public Hologram offset(double value) {
        this.offset = value;
        if(show){
            remove();
            show();
        }
        return this;
    }

    @Override
    public Hologram showTo(Player p) {
        if(!this.players.contains(p.getUniqueId())){
            this.players.add(p.getUniqueId());
        }
        return this;
    }

    @Override
    public Hologram removeTo(Player p) {
        this.players.remove(p.getUniqueId());
        if(this.create.contains(p.getUniqueId())) {
            this.create.remove(p.getUniqueId());
            lines.forEach(stand -> {
                remove(p.getUniqueId(), stand);
            });
        }
        return this;
    }

    @Override
    public void show() {
        update();
        this.players.forEach(uuid -> {
            EntityPlayer ep = ((CraftPlayer) Bukkit.getPlayer(uuid)).getHandle();
            Location loc = this.loc.clone();
            lines.forEach(stand -> {
                stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
                ep.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(stand));
                loc.add(0, -offset, 0);
            });
            this.players.remove(uuid);
        });
        this.show = true;
    }

    private void update(){
        this.create.forEach(uuid -> {
            lines.forEach(stand -> {
                updateMetadata(uuid, stand);
            });
        });
    }

    @Override
    public void remove() {
        if(!this.show){
            try {
                throw new HologramException("§c全息图未创建！");
            } catch (HologramException e) {
                e.printStackTrace();
            }
        }
        this.create.forEach(uuid -> {
            lines.forEach(stand -> {
                remove(uuid, stand);
            });
            this.players.add(uuid);
            this.create.remove(uuid);
        });
        this.show = false;
    }

    @Override
    public Hologram line(String value) {
        EntityArmorStand stand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle());
        stand.setNoGravity(true);
        Player p = null;
        stand.setInvisible(true);
        stand.setSmall(true);
        stand.setCustomNameVisible(true);
        stand.setCustomName(value);
        Collections.reverse(lines);
        this.lines.add(stand);
        Collections.reverse(lines);
        if(show){
            remove();
            show();
        }
        return this;
    }

    @Override
    public Hologram line(int line, String value) {
        EntityArmorStand stand;
        try {
            stand = this.lines.get(line);
            stand.setCustomName(value);
            this.lines.set(line, stand);
        }catch (Exception ex){
            try {
                throw new HologramException("§c此行不存在！");
            } catch (HologramException e) {
                e.printStackTrace();
                return this;
            }
        }
        if(show){
            this.create.forEach(uuid -> {
                updateMetadata(uuid, stand);
            });
        }
        return this;
    }

    @Override
    public Hologram removeLine(int line) {
        this.lines.remove(line);
        return this;
    }

    private void remove(UUID uuid, EntityArmorStand stand){
        EntityPlayer ep = ((CraftPlayer) Bukkit.getPlayer(uuid)).getHandle();
        ep.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(stand.getId()));
    }

    private void updateMetadata(UUID uuid, EntityArmorStand stand){
        EntityPlayer ep = ((CraftPlayer) Bukkit.getPlayer(uuid)).getHandle();
        ep.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true));
    }

}
