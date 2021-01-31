package net.blastmc.onyx.bukkit.support.v1_12_R1;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.api.bukkit.Hologram;
import net.blastmc.onyx.bukkit.util.Method;
import net.blastmc.onyx.bukkit.util.ReflectUtils;
import net.blastmc.onyx.api.bukkit.TeamAction;
import net.blastmc.onyx.bukkit.config.TagConfig;
import net.blastmc.onyx.api.bukkit.NMS;
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
import org.bukkit.craftbukkit.v1_12_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class v1_12_R1 implements NMS {

    @Override
    public Hologram newInstance(Location loc, List<String> list) {
        return new Hologram_1_12_R1(loc, list);
    }

    @Override
    public Hologram newInstance(Location loc, String... lines) {
        return new Hologram_1_12_R1(loc, lines);
    }

    @Override
    public Hologram newInstance(Location loc) {
        return new Hologram_1_12_R1(loc);
    }

    @Override
    public void sendActionBar(Player p, String message) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public Channel getChannel(Player p) {
        return ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel;
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
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        if(action == TeamAction.DESTROY){
            ep.listName = null;
        }else{
            ep.listName = CraftChatMessage.fromString(prefix + p.getName() + suffix)[0];
        }
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
        ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, ep));
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
        PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
        EntityPlayer ep = ((CraftPlayer) p).getHandle();

        //remove vanish
        if(prof.isVanish()){
            for(Player online : Bukkit.getOnlinePlayers()) {
                Method.vanishPlayer(p, online, false);
            }
        }

        //name tag remove
        if (TagConfig.ENABLED && TagConfig.ENABLED_WORLD.contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                String priority = Method.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, priority);
            }
        }

        prof.setNicked(true);

        //remove player
        for (Player online : Bukkit.getOnlinePlayers()){
            online.hidePlayer(p);
        }

        //change name
        Class<?> entityHuman = ep.getClass().getSuperclass();
        try {
            Field bH = entityHuman.getDeclaredField("g");
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
        for (Player online : Bukkit.getOnlinePlayers()){
            online.showPlayer(p);
        }

        //destroy player
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[]{p.getEntityId()});
        for (Player online : Bukkit.getOnlinePlayers())
            if (p != online) ((CraftPlayer) online).getHandle().playerConnection.sendPacket(destroy);

        //name tag restore
        if (TagConfig.ENABLED && TagConfig.ENABLED_WORLD.contains(p.getWorld().getName())) {
            String priority = Method.getTagPriority(p, prof);
            for (Player online : Bukkit.getOnlinePlayers()) {
                String suffix = PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
            }
        }

        //restore player
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(ep);
        for (Player online : Bukkit.getOnlinePlayers())
            if (p != online) ((CraftPlayer) online).getHandle().playerConnection.sendPacket(spawn);

        //restore vanish
        if (prof.isVanish()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                Method.vanishPlayer(p, online, true);
            }
        }
        p.setDisplayName(PlaceholderAPI.setPlaceholders(p, "%profile_prefix%") + p.getName() + PlaceholderAPI.setPlaceholders(p, "%profile_suffix%"));
    }

    @Override
    public void restoreName(Player p) {
        PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        String name = Onyx.getPlayerData(p.getUniqueId()).getName();
        //remove vanish
        if(prof.isVanish()){
            for(Player online : Bukkit.getOnlinePlayers()) {
                Method.vanishPlayer(p, online, false);
            }
        }

        //name tag remove
        if (TagConfig.ENABLED && TagConfig.ENABLED_WORLD.contains(p.getWorld().getName())) {
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
        if (TagConfig.ENABLED && TagConfig.ENABLED_WORLD.contains(p.getWorld().getName())) {
            String priority = Method.getTagPriority(p, prof);
            for (Player online : Bukkit.getOnlinePlayers()) {
                String suffix = PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
            }
        }

        //restore player
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(ep);
        for (Player online : Bukkit.getOnlinePlayers())
            if (p != online) ((CraftPlayer) online).getHandle().playerConnection.sendPacket(spawn);

        //restore vanish
        if (prof.isVanish()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                Method.vanishPlayer(p, online, true);
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

    @Override
    public void hidePlayer(Player p, Player sendTo) {
        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle());
        ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(remove);
    }

    @Override
    public void showPlayer(Player p, Player sendTo) {
        PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) p).getHandle());
        ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(add);
    }

}
