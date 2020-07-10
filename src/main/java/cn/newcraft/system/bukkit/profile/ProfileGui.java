package cn.newcraft.system.bukkit.profile;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.util.interact.ItemBuilder;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.bukkit.gui.PlayerGui;
import cn.newcraft.system.shared.util.SQLHelper;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProfileGui extends PlayerGui {

    private static SQLHelper sql = Main.getSQL();
    private String owner;

    public ProfileGui(Player p, String owner){
        this.owner = owner;
        open(p);
    }

    @Override
    public void open(Player p) {
        UUID uuid = sql.getData("player_data", "player_name", owner, "uuid") == null ? null : UUID.fromString((String) sql.getData("player_data", "player_name", owner, "uuid").get(0));
        if(uuid == null) {
            p.sendMessage("§c玩家 §e" + owner + " §c不是一个 §bNewCraft §c玩家！");
            return;
        }
        String name = (String) sql.getData("player_data", "uuid", uuid.toString(), "player_name").get(0);
        p.sendMessage("§e正在获取数据。。。");
        new BukkitRunnable() {
            @Override
            public void run() {
                List prof = Main.getSQL().getData("player_profile", "uuid", uuid.toString(),
                        "net_level",
                        "net_xp");
                List pData = Main.getSQL().getData("player_data", "uuid", uuid.toString(),
                        "status",
                        "last_leave_mills");
                int netLevel = (int) prof.get(0);
                int netXp = (int) prof.get(1);
                String status = (String) pData.get(0);
                long lastLeave = ((String) pData.get(1)).isEmpty() ? -1 : Long.parseLong((String) pData.get(1));
                Inventory inv = Bukkit.createInventory(null, 54, name + " 的游戏档案");
                initInv(inv);
                ItemStack party = Method.getSkull("http://textures.minecraft.net/texture/345b2edd9ec69a350a867db0e5b0b87551aff498a88e01e2bd6a036ff4d39");
                ItemStack friend = Method.getSkull("http://textures.minecraft.net/texture/76cbae7246cc2c6e888587198c7959979666b4f5a4088f24e26e075f140ae6c3");
                ItemStack guild = Method.getSkull("http://textures.minecraft.net/texture/1765341353c029e9b655f4f57931ae6adc2c7a73c657945d945a307641d3778");
                ItemStack letter = Method.getSkull("http://textures.minecraft.net/texture/b4bd9dd128c94c10c945eadaa342fc6d9765f37b3df2e38f7b056dc7c927ed");
                HashMap map = Method.readUrl("http://api.newcraft.cn:51230/profile/profile.php?uuid=" + uuid.toString());
                ItemStack player = new ItemBuilder(Material.SKULL_ITEM ,1 , (short) 3)
                        .setSkullOwner(name)
                        .setName(map == null ? "" : Method.transColor((String) map.get("prefix")) + name)
                        .addLoreLine("§7NewCraft 等级: " + netLevel)
                        .addLoreLine("§7所在公会: §6暂未开放！")
                        .addLoreLine("")
                        .addLoreLine(getStatus(status, lastLeave))
                        .toItemStack();
                Method.setInvItem(inv, player, 1, 3);
                Method.setInvItem(inv, new ItemBuilder(friend)
                        .setName("§a好友")
                        .addLoreLine("§7添加一个好友或者")
                        .addLoreLine("§7查看你好友的游戏档案！")
                        .toItemStack(), 1, 4);
                Method.setInvItem(inv, new ItemBuilder(party)
                        .setName("§a组队")
                        .addLoreLine("§7创建一个新队伍或者")
                        .addLoreLine("§7查看你当前的队伍状况！")
                        .toItemStack(), 1, 5);
                Method.setInvItem(inv, new ItemBuilder(guild)
                        .setName("§a公会")
                        .addLoreLine("§7组织一个公会并")
                        .addLoreLine("§7与你的公会成员一起游戏！")
                        .toItemStack(), 1, 6);
                Method.setInvItem(inv, new ItemBuilder(letter)
                        .setName("§a邮箱")
                        .addLoreLine("§7查看你目前接收到的邮件")
                        .toItemStack(), 1, 7);
                Method.setInvItem(inv, getBridgeItem(uuid), 3, 4);
                Method.setInvItem(inv, new ItemBuilder(Material.BREWING_STAND_ITEM)
                        .setName("§a大厅等级信息")
                        .addLoreLine("")
                        .addLoreLine("§7NewCraft 等级: " + "§a" + Method.toTrisection(netLevel))
                        .addLoreLine("§7进度: §a" + Method.toTrisection(netLevel) + " " + Method.getProgressBar(netXp, getXpToLevelUp(netLevel), 40, "|", "§b", "§7") + " " + "§a" + Method.getPercent(netXp, getXpToLevelUp(netLevel)))
                        .addLoreLine("")
                        .toItemStack(), 3, 5);
                Method.setInvItem(inv, getArenaItem(uuid), 3, 6);
                p.openInventory(inv);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    private ItemStack getArenaItem(UUID uuid){
        SQLHelper sql = new SQLHelper("localhost:36109", "root", "Mysql_r53Era_2686chen.", "strikepractice");
        return new ItemBuilder(Material.DIAMOND_SWORD)
                .setName("§a竞技场排行信息")
                .addLoreLine("§7无减益模式: §a" + Method.toTrisection(sql.getOrder("stats", "uuid", uuid.toString(), "elo_nodebuffelo")))
                .addLoreLine("§7UHC 模式: §a" + Method.toTrisection(sql.getOrder("stats", "uuid", uuid.toString(), "elo_builduhcelo")))
                .addLoreLine("§7金苹果模式: §a" + Method.toTrisection(sql.getOrder("stats", "uuid", uuid.toString(), "elo_gappleelo")))
                .addLoreLine("§7连击模式: §a" + Method.toTrisection(sql.getOrder("stats", "uuid", uuid.toString(), "elo_comboelo")))
                .addLoreLine("§7空手道模式: §a" + Method.toTrisection(sql.getOrder("stats", "uuid", uuid.toString(), "elo_sumoelo")))
                .addLoreLine("§7全部: §a" + Method.toTrisection(sql.getOrder("stats", "uuid", uuid.toString(), "global_elo")))
                .addLoreLine("§7段位: §a" + getRank((int) sql.getData("stats", "uuid", uuid.toString(), "elo_nodebuffelo").get(0)))
                .toItemStack();
    }

    private ItemStack getBridgeItem(UUID uuid){
        SQLHelper sql = new SQLHelper("localhost:36109", "root", "Mysql_r53Era_2686chen.", "bridgeleveling");
        return new ItemBuilder(Material.SANDSTONE)
                .setName("§a搭路练习信息")
                .addLoreLine("§7等级: " + Method.transColor((String) sql.getData("player_levels", "uuid", uuid.toString(), "pattern_2").get(0)))
                .addLoreLine("§7经验: §b" + Method.toTrisection((int) sql.getData("player_levels", "uuid", uuid.toString(), "xp").get(0)))
                .addLoreLine("§7总方块放置: §a" + Method.toTrisection((int) sql.getData("player_levels", "uuid", uuid.toString(), "totalBlockPlaced").get(0)))
                .addLoreLine("§7最高连杀: §6" + Method.toTrisection((int) sql.getData("player_levels", "uuid", uuid.toString(), "maxKillStreaks").get(0)))
                .addLoreLine("§7总击杀: §a" + Method.toTrisection((int) sql.getData("player_levels", "uuid", uuid.toString(), "totalKill").get(0)))
                .addLoreLine("§7排行: §c" + Method.toTrisection(sql.getOrder("player_levels", "uuid", uuid.toString(), "level")))
                .toItemStack();
    }

    private int getXpToLevelUp(int level){
        return 10000 + (level * 2500);
    }

    private String getStatus(String status, long lastLeave){
        if(lastLeave == -1){
            return "§c未知";
        }
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = dateformat.format(lastLeave);
        if(status.equals("ONLINE")){
            return "§7在线状态: §b在线";
        }else if(status.equals("PLAYING")){
            return "§7在线状态: §b游玩中";
        }else if(status.equals("OFFLINE")){
            return "§7最后一次游玩: §b" + dateStr;
        }
        return "§c未知";
    }

    private void initInv(Inventory inv){
        for(int i = 1; i < 9; i++){
            Method.setInvItem(inv, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 11).setName(" ").toItemStack(), 6, i);
        }
        for(int i = 1; i < 6; i++){
            Method.setInvItem(inv, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 11).setName(" ").toItemStack(), i, 1);
            Method.setInvItem(inv, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 11).setName(" ").toItemStack(), i, 9);
        }
        Method.setInvItem(inv, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 11).setName(" ").toItemStack(), 1, 2);
        Method.setInvItem(inv, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 11).setName(" ").toItemStack(), 1, 8);
        Method.setInvItem(inv, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 11).setName(" ").toItemStack(), 6, 9);
        Method.setInvItem(inv, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack(), 2, 8);
        for(int i = 2; i < 8; i++){
            Method.setInvItem(inv, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").toItemStack(), 2, i);
        }
        Method.setInvItem(inv, new ItemBuilder(Material.BARRIER)
                .setName("§e关闭菜单")
                .addLoreLine("§7点击这里，关闭界面！").toItemStack(), 6, 5);
    }

    private String getRank(int elo){
        int order = elo;
        if (order <= 300){
            return "§6倔强青铜 III";
        }
        if (order <= 500){
            return "§6倔强青铜 II";
        }
        if (order <= 700){
            return "§6倔强青铜 I";
        }
        if (order <= 900){
            return "§f秩序白银 V";
        }
        if (order <= 1100){
            return "§f秩序白银 IV";
        }
        if (order <= 1300){
            return "§f秩序白银 III";
        }
        if (order <= 1500){
            return "§f秩序白银 II";
        }
        if (order <= 1800){
            return "§f秩序白银 I";
        }
        if (order <= 2000){
            return "§e荣耀黄金 V";
        }
        if (order <= 2200){
            return "§e荣耀黄金 IV";
        }
        if (order <= 2400){
            return "§e荣耀黄金 III";
        }
        if (order <= 2600){
            return "§e荣耀黄金 II";
        }
        if (order <= 2900){
            return "§e荣耀黄金 I";
        }
        if (order <= 3100){
            return "§3尊贵铂金 V";
        }
        if (order <= 3300){
            return "§3尊贵铂金 IV";
        }
        if (order <= 3500){
            return "§3尊贵铂金 III";
        }
        if (order <= 3700){
            return "§3尊贵铂金 II";
        }
        if (order <= 4000){
            return "§3尊贵铂金 I";
        }
        if (order <= 4200){
            return "§b永恒钻石 V";
        }
        if (order <= 4400){
            return "§b永恒钻石 IV";
        }
        if (order <= 4600){
            return "§b永恒钻石 III";
        }
        if (order <= 4800){
            return "§b永恒钻石 II";
        }
        if (order <= 5100){
            return "§b永恒钻石 I";
        }
        if (order <= 5400){
            return "§d至尊星耀 V";
        }
        if (order <= 5700){
            return "§d至尊星耀 IV";
        }
        if (order <= 6000){
            return "§d至尊星耀 III";
        }
        if (order <= 6300){
            return "§d至尊星耀 II";
        }
        if (order <= 6600){
            return "§d至尊星耀 I";
        }
        return "§c未知";
    }

}
