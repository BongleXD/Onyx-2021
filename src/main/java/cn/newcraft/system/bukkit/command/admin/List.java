package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class List extends CommandManager {
    public List() {
        super("list", "显示在线玩家", "/list");
    }

    @Cmd(perm = "ncs.command.list")
    public void list(CommandSender sender, String[] args){
        int players = Bukkit.getOnlinePlayers().size();
        sender.sendMessage("§9§m--------------------------");
        sender.sendMessage("§e当前在线玩家：  §8| §6§l" + players + " §ePlayers");
        sender.sendMessage("§e世界 §8| §e玩家名");
        for (Player player : Bukkit.getOnlinePlayers()) {
            sender.sendMessage("§6" + player.getWorld().getName() + " §8| §6" + player.getDisplayName());
        }
        sender.sendMessage("§9§m--------------------------");
    }
}
