package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.util.interact.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class Lightingstick extends CommandManager implements Listener {

    public Lightingstick() {
        super("lightningstick", "闪电棍", "/lightningstick <give/take> <玩家/all>", "闪电棍", "ls");
        this.setPermissionMessage("§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("闪电棍")) e.setCancelled(true);
    }

    @EventHandler
    public void onShock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getItemInHand();
        if (item == null || item.getType() == Material.AIR || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
            return;
        }
        if (p.getItemInHand().getItemMeta().getDisplayName().contains("闪电棍")) {
            p.getWorld().strikeLightningEffect(p.getTargetBlock(null, 200).getLocation());
        }
    }

    @EventHandler
    public void onLoggout(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
                continue;
            }
            if (item.getItemMeta().getDisplayName().contains("闪电棍")) {
                p.getInventory().remove(item);
                p.sendMessage("§c你的闪电棍已被回收！");
                break;
            }
        }
    }

    @Cmd(arg = "give <player>", aliases = "给予", perm = "ncs.command.lightningstick", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void givePlayer(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[1]);
        boolean b = false;
        for(ItemStack item : p.getInventory().getContents()) {
            try {
                if (item.getItemMeta().getDisplayName().contains("闪电棍")) {
                    b = true;
                    break;
                }
            }catch (NullPointerException ignored){ }
        }
        if(!b) {
            p.getInventory().addItem(new ItemBuilder(Material.STICK).setName("§b闪电棍").toItemStack());
            sender.sendMessage("§a已给予玩家 " + p.getDisplayName() + " §a一根闪电棍！");
            if (p != sender) {
                p.sendMessage("§a一名管理员给予了你一根闪电棍！");
            }
        }else{
            sender.sendMessage("§c玩家 " + p.getDisplayName() + " §c已拥有一根闪电棍！");
        }
    }

    @Cmd(arg = "give all", aliases = "给予 所有玩家", perm = "ncs.command.lightningstick", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void giveAll(CommandSender sender, String[] args){
        for(Player online : Bukkit.getOnlinePlayers()) {
            boolean b = false;
            for(ItemStack item : online.getInventory().getContents()) {
                try {
                    if (item.getItemMeta().getDisplayName().contains("闪电棍")) {
                        b = true;
                        break;
                    }
                }catch (NullPointerException ignored){ }
            }
            if(!b){
                online.getInventory().addItem(new ItemBuilder(Material.STICK).setName("§b闪电棍").toItemStack());
                if (online != sender) {
                    online.sendMessage("§a一名管理员给予了你一根闪电棍！");
                }
            }
        }
        sender.sendMessage("§a已给予所有玩家一根闪电棍！");
    }

    @Cmd(arg = "take <player>", aliases = "取回", perm = "ncs.command.lightningstick", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void takePlayer(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[1]);
        for(ItemStack item : p.getInventory().getContents()){
            try {
                if (item.getItemMeta().getDisplayName().contains("闪电棍")) {
                    p.getInventory().remove(item);
                    sender.sendMessage("§a已回收玩家 " + p.getDisplayName() + " §a的闪电棍！");
                    if (p != sender) {
                        p.sendMessage("§c一名管理员已回收你的闪电棍！");
                    }
                    return;
                }
            }catch (NullPointerException ignored){ }
        }
        sender.sendMessage("§c未检测到玩家 " + p.getDisplayName() + " §c拥有闪电棍！");
    }

    @Cmd(arg = "take all", aliases = "取回 所有玩家", perm = "ncs.command.lightningstick", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void takeAll(CommandSender sender, String[] args){
        for(Player online : Bukkit.getOnlinePlayers()) {
            for(ItemStack item : online.getInventory().getContents()){
                try {
                    if (item.getItemMeta().getDisplayName().contains("闪电棍")) {
                        online.getInventory().remove(item);
                        if (online != sender) {
                            online.sendMessage("§c一名管理员已回收你的闪电棍！");
                        }
                        break;
                    }
                }catch (NullPointerException ignored){ }
            }
        }
        sender.sendMessage("§a已回收所有玩家的闪电棍！");
    }

}
