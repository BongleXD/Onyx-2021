package cn.newcraft.system.bukkit.support.v1_12_R1;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.config.TagConfig;
import cn.newcraft.system.bukkit.support.NMS;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.bukkit.util.ReflectUtils;
import cn.newcraft.system.bukkit.util.TeamAction;
import cn.newcraft.system.shared.PlayerData;
import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import static cn.newcraft.system.bukkit.util.Method.getTagData;
import static cn.newcraft.system.bukkit.util.Method.vanishPlayer;

public class v1_12_R1 implements NMS {

    @Override
    public void sendActionBar(Player p, String message) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public Channel getChannel(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        return ep.playerConnection.networkManager.channel;
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
        ScoreboardTeam team = new Scoreboard().createTeam(teamName);
        team.setDisplayName(p.getName());
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS);
        PacketPlayOutScoreboardTeam packet = null;
        switch (action) {
            case CREATE:
                team.getPlayerNameSet().add(p.getName());
                packet = new PacketPlayOutScoreboardTeam(team, 0);
                break;
            case UPDATE:
                packet = new PacketPlayOutScoreboardTeam(team, 2);
                break;
            case DESTROY:
                packet = new PacketPlayOutScoreboardTeam(team, 1);
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
            net.minecraft.server.v1_12_R1.ItemStack nmsBook = CraftItemStack.asNMSCopy(book);
            p.setItemInHand(book);
            ep.a(nmsBook, EnumHand.MAIN_HAND);
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

        //update list
        try {
            MinecraftServer server = MinecraftServer.getServer();
            PlayerList list = server.getPlayerList();
            Field f = ReflectUtils.getNMSClass("PlayerList").getDeclaredField("playersByName");
            f.setAccessible(true);
            Map<String, Object> map = (Map<String, Object>) f.get(list);
            ArrayList<String> toRemove = new ArrayList<>();
            for (String cachedName : map.keySet()) {
                if(cachedName != null) {
                    Object entityPlayer = map.get(cachedName);
                    if((entityPlayer == null) || entityPlayer.getClass().getMethod("getUniqueID").invoke(entityPlayer).equals(p.getUniqueId()))
                        toRemove.add(cachedName);
                }
            }
            for (String string : toRemove) map.remove(string);
            map.put(p.getName(), p.getClass().getMethod("getHandle").invoke(p));
            f.set(list, map);
            f.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
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
        Method.setSkin(p, prof.getNickSkin());

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                reloadPlayer(p);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void restoreName(Player p) {
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        String name = PlayerData.getDataFromUUID(p.getUniqueId()).getName();

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
            bH.set(ep, new GameProfile(p.getUniqueId(), name));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        //update list
        try {
            MinecraftServer server = MinecraftServer.getServer();
            PlayerList list = server.getPlayerList();
            Field f = ReflectUtils.getNMSClass("PlayerList").getDeclaredField("playersByName");
            f.setAccessible(true);
            Map<String, Object> map = (Map<String, Object>) f.get(list);
            ArrayList<String> toRemove = new ArrayList<>();
            for (String cachedName : map.keySet()) {
                if(cachedName != null) {
                    Object entityPlayer = map.get(cachedName);
                    if((entityPlayer == null) || entityPlayer.getClass().getMethod("getUniqueID").invoke(entityPlayer).equals(p.getUniqueId()))
                        toRemove.add(cachedName);
                }
            }
            for (String string : toRemove) map.remove(string);
            map.put(p.getName(), p.getClass().getMethod("getHandle").invoke(p));
            f.set(list, map);
            f.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
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
