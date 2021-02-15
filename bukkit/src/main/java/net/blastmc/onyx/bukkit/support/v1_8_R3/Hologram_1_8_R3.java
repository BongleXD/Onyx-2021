package net.blastmc.onyx.bukkit.support.v1_8_R3;

import me.clip.placeholderapi.PlaceholderAPI;
import net.blastmc.onyx.api.bukkit.Animation;
import net.blastmc.onyx.bukkit.exception.HologramException;
import net.blastmc.onyx.api.bukkit.Hologram;
import com.google.common.collect.Lists;
import net.blastmc.onyx.bukkit.hologram.AnimData;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Hologram_1_8_R3 implements Hologram {

    private Location loc;
    private List<UUID> players;
    private List<UUID> create;
    private List<HoloData> lines;
    private double offset = 0.23D;
    private boolean show = false;
    private HashMap<Integer, Animation> animMap = new HashMap<>();
    private HashMap<Integer, AnimData> drawMap = new HashMap<>();

    public Hologram_1_8_R3(Location loc, String... lines){
        this.loc = loc;
        this.lines = Lists.newArrayList();
        this.players = Lists.newArrayList();
        this.create = Lists.newArrayList();
        for (String line : lines) {
            EntityArmorStand stand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle());
            stand.setGravity(false);
            stand.setInvisible(true);
            stand.setSmall(true);
            stand.setCustomNameVisible(true);
            stand.setCustomName(line);
            this.lines.add(new HoloData(line, stand));
        }
    }

    public Hologram_1_8_R3(Location loc){
        this.loc = loc;
        this.lines = Lists.newArrayList();
        this.players = Lists.newArrayList();
        this.create = Lists.newArrayList();
    }

    public Hologram_1_8_R3(Location loc, List<String> list) {
        this.loc = loc;
        this.lines = Lists.newArrayList();
        this.players = Lists.newArrayList();
        this.create = Lists.newArrayList();
        for (String line : list) {
            EntityArmorStand stand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle());
            stand.setGravity(false);
            stand.setInvisible(true);
            stand.setSmall(true);
            stand.setCustomNameVisible(true);
            stand.setCustomName(line);
            this.lines.add(new HoloData(line, stand));
        }
    }


    @Override
    public Hologram animation(int line, Animation anim) {
        animMap.put(line, anim);
        return this;
    }

    @Override
    public List<String> getLines() {
        return lines.stream()
                .map(data -> data.line)
                .collect(Collectors.toList());
    }

    @Override
    public Location getLocation() {
        return loc;
    }

    @Override
    public Hologram location(Location loc) {
        this.loc = loc;
        Location newLoc = this.loc.clone();
        this.create.forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null){
                lines.stream()
                        .map(data -> data.stand)
                        .forEach(stand -> {
                            stand.setPosition(loc.getX(), loc.getY(), loc.getZ());
                            EntityPlayer ep = ((CraftPlayer) p).getHandle();
                            ep.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(stand));
                            newLoc.add(0, -offset, 0);
                        });
            }
        });
        return this;
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
    public Hologram showTo(Collection<? extends Player> list) {
        for(Player p : list){
            if(!this.players.contains(p.getUniqueId())){
                this.players.add(p.getUniqueId());
            }
        }
        return this;
    }

    @Override
    public Hologram removeTo(Player p) {
        this.players.remove(p.getUniqueId());
        if(this.create.contains(p.getUniqueId())) {
            lines.stream().map(data -> data.stand).forEach(stand -> {
                remove(p.getUniqueId(), stand);
            });
            this.create.remove(p.getUniqueId());
        }
        return this;
    }

    @Override
    public Hologram removeTo(Collection<? extends Player> list) {
        for(Player p : list){
            this.players.remove(p.getUniqueId());
            if(this.create.contains(p.getUniqueId())) {
                lines.stream().map(data -> data.stand).forEach(stand -> {
                    remove(p.getUniqueId(), stand);
                });
                this.create.remove(p.getUniqueId());
            }
        }
        return this;
    }

    @Override
    public void show() {
        for(int i = 0; i < lines.size(); i++){
            if(animMap.containsKey(i) && !drawMap.containsKey(i)){
                Animation.Value value = animMap.get(i).getValueList().get(0);
                drawMap.put(i, new AnimData(0, value.line, value.frame));
            }
        }
        update();
        for(Iterator<UUID> it = this.players.iterator(); it.hasNext();){
            UUID uuid = it.next();
            Player p = Bukkit.getPlayer(uuid);
            if(p == null){
                continue;
            }
            EntityPlayer ep = ((CraftPlayer) p).getHandle();
            Location loc = this.loc.clone();
            for(int i = 0; i < lines.size(); i++){
                HoloData data = lines.get(i);
                EntityArmorStand stand = data.stand;
                stand.setPosition(loc.getX(), loc.getY(), loc.getZ());
                if(animMap.containsKey(i)) {
                    if (drawMap.get(i).frame <= 0) {
                        AnimData animData = drawMap.get(i);
                        int id = animData.id + 1 >= animMap.get(i).getValueList().size() ? 0 : animData.id + 1;
                        Animation.Value value = animMap.get(i).getValueList().get(id);
                        drawMap.put(i, new AnimData(id, value.line, value.frame));
                    }
                }
                stand.setCustomName(animMap == null || animMap.isEmpty() ? data.line : "§f读取中...");
                ep.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(stand));
                update(uuid);
                loc.add(0, -offset, 0);
            }
            this.create.add(uuid);
            it.remove();
        }
        this.show = true;
    }

    private void update(UUID uuid) {
        Location loc = this.loc.clone();
        for (int i = 0; i < lines.size(); i++) {
            if (animMap.containsKey(i)) {
                AnimData animData = drawMap.get(i);
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    updateMetadata(uuid, animData.line, i);
                }
            } else {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    updateMetadata(uuid, lines.get(i).line, i);
                }
            }
            loc.add(0, -offset, 0);
        }
    }

    private void update() {
        Location loc = this.loc.clone();
        for (int i = 0; i < lines.size(); i++) {
            if (animMap.containsKey(i)) {
                if (drawMap.get(i).frame <= 0) {
                    AnimData animData = drawMap.get(i);
                    for (UUID uuid : create) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) {
                            updateMetadata(uuid, animData.line, i);
                        }
                    }
                    int id = animData.id + 1 >= animMap.get(i).getValueList().size() ? 0 : animData.id + 1;
                    Animation.Value value = animMap.get(i).getValueList().get(id);
                    drawMap.put(i, new AnimData(id, value.line, value.frame));
                } else {
                    AnimData animData = drawMap.get(i);
                    animData.frame--;
                    drawMap.put(i, animData);
                }
            } else {
                for (UUID uuid : create) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        updateMetadata(uuid, lines.get(i).line, i);
                    }
                }
            }
            loc.add(0, -offset, 0);
        }
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
        for(Iterator<UUID> it = this.create.iterator(); it.hasNext();){
            UUID uuid = it.next();
            lines.stream().map(data -> data.stand).forEach(stand -> {
                remove(uuid, stand);
            });
            this.players.add(uuid);
            it.remove();
        }
        this.show = false;
    }

    @Override
    public Hologram line(String value) {
        EntityArmorStand stand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle());
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setSmall(true);
        stand.setCustomNameVisible(true);
        stand.setCustomName(value);
        Collections.reverse(lines);
        this.lines.add(new HoloData(value, stand));
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
            HoloData data = this.lines.get(line);
            stand = data.stand;
            stand.setCustomName(value);
            data.stand = stand;
            data.line = stand.getCustomName();
            this.lines.set(line, data);
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
                updateMetadata(uuid, value, line);
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

    private void updateMetadata(UUID uuid, String line, int i){
        Player p = Bukkit.getPlayer(uuid);
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        EntityArmorStand stand = lines.get(i).stand;
        stand.setCustomName(PlaceholderAPI.setPlaceholders(p, line));
        ep.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true));
    }

    protected class HoloData {

        String line;
        EntityArmorStand stand;

        HoloData(String line, EntityArmorStand stand) {
            this.line = line;
            this.stand = stand;
        }

    }

}
