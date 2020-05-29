package cn.newcraft.system.bukkit.support.v1_8_R3;

import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.config.TagConfig;
import cn.newcraft.system.bukkit.support.NMS;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.bukkit.util.ReflectUtils;
import cn.newcraft.system.bukkit.util.TeamAction;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import static cn.newcraft.system.bukkit.util.Method.getTagData;
import static cn.newcraft.system.bukkit.util.Method.vanishPlayer;

public class v1_8_R3 implements NMS {

    @Override
    public void sendActionBar(Player p, String message) {
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" +
                ChatColor.translateAlternateColorCodes('&', message) + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte)2);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(bar);
    }

    @Override
    public String levelUP() {
        return "LEVEL_UP";
    }

    @Override
    public String joinSound(){
        return "NOTE_PLING";
    }

    @Override
    public String quitSound(){
        return "NOTE_BASS";
    }

    @Override
    public String NOTE_STICKS(){
        return "NOTE_STICKS";
    }

    @Override
    public void sendFootStep(Location loc) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FOOTSTEP,true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0, 0, 0, 0, 1);
        for(Player online : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void changeNameTag(Player sendTo, Player p, String prefix, String suffix, TeamAction action, String priority) {
        if(prefix.length() >= 16){
            prefix = prefix.substring(0, 16);
        }
        if(suffix.length() >= 16){
            suffix = suffix.substring(0, 16);
        }
        String teamName = (priority + p.getName());
        if(teamName.length() >= 16){
            teamName = teamName.substring(0, 16);
        }
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        try {
            ReflectUtils.setField(packet, "a", teamName);
            ReflectUtils.setField(packet,"b", p.getName());
            ReflectUtils.setField(packet,"c", prefix);
            ReflectUtils.setField(packet,"d", suffix);
            ReflectUtils.setField(packet,"e", ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e);
            switch (action) {
                case CREATE:
                    ReflectUtils.setField(packet, "g", Collections.singleton(p.getName()));
                    break;
                case UPDATE:
                    ReflectUtils.setField(packet, "h", 2);
                    break;
                case DESTROY:
                    ReflectUtils.setField(packet, "h", 1);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void crashClient(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        PacketPlayOutExplosion packet = new PacketPlayOutExplosion(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Float.MAX_VALUE, new ArrayList<BlockPosition>(), new Vec3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE));
        ep.playerConnection.sendPacket(packet);
    }

    @Override
    public void openBookMenu(Player p, ItemStack book) {
        ItemStack hand = p.getItemInHand();
        try {
            EntityPlayer ep = ((CraftPlayer) p).getHandle();
            p.setItemInHand(book);
            ep.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(Unpooled.buffer())));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            p.setItemInHand(hand);
        }
    }

    @Override
    public void changeName(Player p, String name) {
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        EntityPlayer ep = ((CraftPlayer) p).getHandle();

        //remove vanish
        if(prof.isVanish()){
            for(Player online : Bukkit.getOnlinePlayers()) {
                vanishPlayer(p, online, false);
            }
        }

        //name tag remove
        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                String priority = Method.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, priority);
            }
        }

        prof.setNicked(true);

        //remove player
        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        Bukkit.getOnlinePlayers().forEach(online -> { ((CraftPlayer) online).getHandle().playerConnection.sendPacket(remove); });

        //change name
        Class<?> entityHuman = ep.getClass().getSuperclass();
        try {
            Field bH = entityHuman.getDeclaredField("bH");
            bH.setAccessible(true);
            bH.set(ep, new GameProfile(p.getUniqueId(), name));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        /*CraftServer server = (CraftServer) Bukkit.getServer();
        Object list = ReflectUtils.getField(server.getClass(), "playerList");
        
         */

        //add player
        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        Bukkit.getOnlinePlayers().forEach(online -> {
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(add);
        });

        //destroy player
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[]{p.getEntityId()});
        for (Player online : Bukkit.getOnlinePlayers())
            if (p != online) ((CraftPlayer) online).getHandle().playerConnection.sendPacket(destroy);

        //name tag restore
        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            String priority = Method.getTagPriority(p, prof);
            for (Player online : Bukkit.getOnlinePlayers()) {
                String suffix = PlaceholderAPI.setPlaceholders(p, getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
            }
        }

        //restore player
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(ep);
        for (Player online : Bukkit.getOnlinePlayers())
            if (p != online) ((CraftPlayer) online).getHandle().playerConnection.sendPacket(spawn);

        //restore vanish
        if (prof.isVanish()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                vanishPlayer(p, online, true);
            }
        }
        p.setDisplayName(PlaceholderAPI.setPlaceholders(p, "%profile_prefix%") + p.getName() + PlaceholderAPI.setPlaceholders(p, "%profile_suffix%"));
    }

    @Override
    public void restoreName(Player p) {
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        EntityPlayer ep = ((CraftPlayer) p).getHandle();

        if(!p.hasPermission("ncs.nick.staff")){
            Method.setSkin(p, p.getName());
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        if (!p.isOnline()) {
                            return;
                        }
                        ep.server.getPlayerList().moveToWorld(ep, ep.dimension, false, p.getLocation(), false);
                    }, 5L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            return;
        }
        //remove vanish
        if(prof.isVanish()){
            for(Player online : Bukkit.getOnlinePlayers()) {
                vanishPlayer(p, online, false);
            }
        }

        //name tag remove
        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                String priority = Method.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, priority);
            }
        }

        //remove player
        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        Bukkit.getOnlinePlayers().forEach(online -> { ((CraftPlayer) online).getHandle().playerConnection.sendPacket(remove); });

        //change name
        Class<?> entityHuman = ep.getClass().getSuperclass();
        try {
            Field bH = entityHuman.getDeclaredField("bH");
            bH.setAccessible(true);
            bH.set(ep, new GameProfile(p.getUniqueId(), PlayerData.getDataFromUUID(p.getUniqueId()).getName()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        //add player
        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
        Bukkit.getOnlinePlayers().forEach(online -> {
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(add);
        });

        //destroy player
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[]{p.getEntityId()});
        for (Player online : Bukkit.getOnlinePlayers())
            if (p != online) ((CraftPlayer) online).getHandle().playerConnection.sendPacket(destroy);

        prof.setNicked(false);
        prof.setNickPrefix("");
        prof.setNickSkin("");
        prof.setNickName("");

        //name tag restore
        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            String priority = Method.getTagPriority(p, prof);
            for (Player online : Bukkit.getOnlinePlayers()) {
                String suffix = PlaceholderAPI.setPlaceholders(p, getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
            }
        }

        //restore player
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(ep);
        for (Player online : Bukkit.getOnlinePlayers())
            if (p != online) ((CraftPlayer) online).getHandle().playerConnection.sendPacket(spawn);

        //restore vanish
        if (prof.isVanish()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                vanishPlayer(p, online, true);
            }
        }
        p.setDisplayName(PlaceholderAPI.setPlaceholders(p, "%profile_prefix%") + p.getName() + PlaceholderAPI.setPlaceholders(p, "%profile_suffix%"));
        Method.setSkin(p, p.getName());
        new Thread(() -> {
            try {
                Thread.sleep(500);
                reloadPlayer(p);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void reloadPlayer(Player p) {
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            EntityPlayer ep = ((CraftPlayer) p).getHandle();
            if (!p.isOnline()) {
                return;
            }
            //remove player
            PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep);
            Bukkit.getOnlinePlayers().forEach(online -> { ((CraftPlayer) online).getHandle().playerConnection.sendPacket(remove); });

            ep.server.getPlayerList().moveToWorld(ep, ep.dimension, false, p.getLocation(), false);

            //add player
            PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep);
            Bukkit.getOnlinePlayers().forEach(online -> {
                ((CraftPlayer) online).getHandle().playerConnection.sendPacket(add);
            });

            //destroy player
            PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[]{p.getEntityId()});
            for (Player online : Bukkit.getOnlinePlayers())
                if (p != online) ((CraftPlayer) online).getHandle().playerConnection.sendPacket(destroy);

            //restore player
            PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(ep);
            for (Player online : Bukkit.getOnlinePlayers())
                if (p != online) ((CraftPlayer) online).getHandle().playerConnection.sendPacket(spawn);
        });
    }

}
