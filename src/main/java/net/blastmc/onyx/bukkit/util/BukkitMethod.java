package net.blastmc.onyx.bukkit.util;

import net.blastmc.onyx.bukkit.api.PlayerProfile;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.api.TagData;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.*;

public class BukkitMethod {

    private static HashMap<String, BukkitTask> taskMap = new HashMap<>();

    public static BukkitTask getTask(String task){
        return taskMap.get(task);
    }

    public static void createTask(String name, BukkitTask task){
        taskMap.put(name, task);
    }

    public static void removeTask(String name){
        if(taskMap.containsKey(name)){
            taskMap.get(name).cancel();
            taskMap.remove(name);
        }
    }

    public static void setInvItem(Inventory inv, ItemStack item, int row, int column){
        inv.setItem((row - 1) *  9 + column - 1, item);
    }


    public static ItemStack getSkull(String url) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field f = null;
        try {
            f = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static void setSkin(Player p, String skinName){
        ByteArrayDataOutput b = ByteStreams.newDataOutput();
        b.writeUTF("CHANGE_SKIN");
        b.writeUTF((String) Main.getSQL().getData("player_data", "uuid", p.getUniqueId().toString(),"name").get(0));
        b.writeUTF(skinName);
        p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static void vanishPlayer(Player p, Player sendTo, boolean b) {
        if(b){
            if (!sendTo.hasPermission("onyx.vanish.bypass") && p != sendTo) {
                sendTo.hidePlayer(p);
            }else{
                sendTo.showPlayer(p);
            }
            Main.getNMS().changeNameTag(sendTo, p, PlaceholderAPI.setPlaceholders(p, BukkitMethod.getTagData(p).getPrefix()), " §c[已隐身]", TeamAction.UPDATE, getTagPriority(p, PlayerProfile.getDataFromUUID(p.getUniqueId())));
        }else{
            if(p != sendTo){
                sendTo.showPlayer(p);
            }
            Main.getNMS().changeNameTag(sendTo, p, PlaceholderAPI.setPlaceholders(p, BukkitMethod.getTagData(p).getPrefix()), PlaceholderAPI.setPlaceholders(p, BukkitMethod.getTagData(p).getSuffix()), TeamAction.UPDATE, getTagPriority(p, PlayerProfile.getDataFromUUID(p.getUniqueId())));
        }
    }

    public static String getTagPriority(Player p, PlayerProfile prof) {
        String priority;
        if(prof == null){
            return null;
        }
        if(!prof.isNicked()){
            return getTagData(p).getPriority();
        }
        switch (prof.getNickPrefix()) {
            case "§6[SVIP§c+§6] ":
                priority = TagData.getData("svip_plus").getPriority();
                break;
            case "§b[SVIP] ":
                priority = TagData.getData("svip").getPriority();
                break;
            case "§a[VIP§6+§a] ":
                priority = TagData.getData("vip_plus").getPriority();
                break;
            case "§a[VIP] ":
                priority = TagData.getData("vip").getPriority();
                break;
            case "§7":
                priority = TagData.getData("default").getPriority();
                break;
            default:
                priority = getTagData(p).getPriority();
        }
        return priority;
    }

    public static String consolidateStrings(String[] args, int start) {
        StringBuilder ret = new StringBuilder(args[start]);
        if (args.length > start + 1) {
            for (int i = start + 1; i < args.length; i++) {
                ret.append(" ").append(args[i]);
            }
        }
        return ret.toString();
    }

    public static TagData getTagData(Player p){
        TagData tagData = TagData.getData("default");
        int priority = tagData.getIntegerPriority();
        for(String group : TagData.getTagGroups()){
            int pri = TagData.getData(group).getIntegerPriority();
            if(TagData.getData(group).getPerm().isEmpty()){
                if(pri < priority) {
                    tagData = TagData.getData(group);
                    priority = pri;
                }
            }else if(p.hasPermission((TagData.getData(group).getPerm()))){
                if(pri < priority) {
                    tagData = TagData.getData(group);
                    priority = pri;
                }
            }
        }
        return tagData;
    }

}
