package cn.newcraft.system.bukkit.util;

import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.TagData;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

public class Method {

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

    public static String getPercent(double x, double y){
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("0.0");
        return df.format(x / y * 100).replace(".0", "") + "%";
    }

    public static String transColor(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String toTrisection(double d) {
        if (d == 0) {
            return "0";
        }
        DecimalFormat df = new DecimalFormat("#,###.00");
        String result = df.format(d).replace(".00", "");
        return result.endsWith(".0") ? result.replace(".0", "") : result.contains(".") && result.endsWith("0") ? result.substring(0, result.length() - 1) : result;
    }

    public static String toKilo(int number){
        int i = number / 100;
        if(number < 1000){
            return String.valueOf(number);
        }
        else if(i % 10 == 0){
            return number / 1000 + "k";
        }
        else if(number % 1000 == 0){
            return number / 1000 + "k";
        }
        else if(number > 1000){
            double d = (double) number / (double) 100;
            d = Math.floor(d);
            return d / 10 + "k";
        }
        else{
            return String.valueOf(number);
        }
    }

    public static void setInvItem(Inventory inv, ItemStack item, int row, int column){
        inv.setItem((row - 1) *  9 + column - 1, item);
    }

    public static String toRoman(int number) {
        StringBuilder rNumber = new StringBuilder();
        int[] aArray = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] rArray = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X",
                "IX", "V", "IV", "I"};
        if (number < 1 || number > 3999) {
            rNumber = new StringBuilder();
        } else {
            for (int i = 0; i < aArray.length; i++) {
                while (number >= aArray[i]) {
                    rNumber.append(rArray[i]);
                    number -= aArray[i];
                }
            }
        }
        return rNumber.toString();
    }

    public static String getProgressBar(int xp, int xpToLevelUp, int length, String symbol, String unlock, String lock) {
        StringBuilder sb = new StringBuilder();
        if (xpToLevelUp == 0) {
            return "";
        }
        int max = xp * length / xpToLevelUp;
        for (int i = 0; i < length; i++) {
            if (i < max) {
                sb.append(unlock).append(symbol);
            } else {
                sb.append(lock).append(symbol);
            }
        }
        return sb.toString();
    }


    public static double roundDouble(double data, int scale){
        return new BigDecimal(data).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
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

    public static HashMap readUrl(String urlString) {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
            HashMap map = new HashMap<>();
            map = new Gson().fromJson(buffer.toString(), map.getClass());
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void setSkin(Player p, String skinName){
        ByteArrayDataOutput b = ByteStreams.newDataOutput();
        b.writeUTF("CHANGE_SKIN");
        b.writeUTF((String) Main.getSQL().getData("player_data", "uuid", p.getUniqueId().toString(),"player_name").get(0));
        b.writeUTF(skinName);
        p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static void vanishPlayer(Player p, Player sendTo, boolean b) {
        if(b){
            if (!sendTo.hasPermission("ncs.vanish.bypass") && p != sendTo) {
                sendTo.hidePlayer(p);
            }else{
                sendTo.showPlayer(p);
            }
            Main.getNMS().changeNameTag(sendTo, p, PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getPrefix()), " §c[已隐身]", TeamAction.UPDATE, getTagPriority(p, PlayerProfile.getDataFromUUID(p.getUniqueId())));
        }else{
            if(p != sendTo){
                sendTo.showPlayer(p);
            }
            Main.getNMS().changeNameTag(sendTo, p, PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getPrefix()), PlaceholderAPI.setPlaceholders(p, Method.getTagData(p).getSuffix()), TeamAction.UPDATE, getTagPriority(p, PlayerProfile.getDataFromUUID(p.getUniqueId())));
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
            case "§6[MVP§c++§6] ":
                priority = TagData.getData("mvp_plus_plus").getPriority();
                break;
            case "§b[MVP§c+§b] ":
                priority = TagData.getData("mvp_plus").getPriority();
                break;
            case "§b[MVP] ":
                priority = TagData.getData("mvp").getPriority();
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
