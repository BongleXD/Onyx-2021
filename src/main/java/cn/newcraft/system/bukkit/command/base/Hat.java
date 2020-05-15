package cn.newcraft.system.bukkit.command.base;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Hat extends CommandManager {

    public Hat() {
        super("hat", "将物品戴在头上", "/hat");
    }

    @Cmd(perm = "ncs.command.hat", only = CommandOnly.PLAYER)
    public void hat(CommandSender sender, String[] args){
        Player p = (Player)sender;
        PlayerInventory inv = p.getInventory();
        ItemStack held = inv.getItemInHand();
        ItemStack helm = inv.getHelmet();
        if (held.getType() == Material.AIR) {
            p.sendMessage("§c佩戴失败，你并未手持任何物品！");
            return;
        }
        inv.setHelmet(held);
        inv.setItemInHand(helm);
        p.updateInventory();
        p.sendMessage("§a享受你的新帽子吧！");
    }
}
