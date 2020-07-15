package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.api.PlayerProfile;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.shared.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class List extends CommandManager {
    public List() {
        super("list", "显示在线玩家", "/list");
    }

    @Cmd(perm = "onyx.command.list")
    public void list(CommandSender sender, String[] args){
        int players = Bukkit.getOnlinePlayers().size();
        sender.sendMessage("§9§m--------------------------");
        sender.sendMessage("§e当前在线玩家：  §8| §6§l" + players + " §ePlayers");
        sender.sendMessage("§e世界 §8| §e玩家名");
        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerData data = PlayerData.getDataFromUUID(online.getUniqueId());
            PlayerProfile prof = PlayerProfile.getDataFromUUID(online.getUniqueId());
            sender.sendMessage("§6" + online.getWorld().getName() + " §8| §6" + online.getDisplayName() + (prof != null && data != null && prof.isNicked() ? " §7(原ID： " + data.getName() + ")" : ""));
        }
        sender.sendMessage("§9§m--------------------------");
    }
}
