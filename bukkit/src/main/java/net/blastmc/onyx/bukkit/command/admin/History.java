package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class History extends CommandManager {

    public History() {
        super("history", "惩罚历史", "/history <玩家> [页数]", "onyx.command.history");
    }

    @Cmd(arg = "<player> <integer>", perm = "onyx.command.history", permMessage = "§c你需要 §2客服 及以上的会员等级才能使用此指令！")
    public void history(CommandSender sender, String[] args){
        sender.sendMessage("§e正在查询中...");
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            String pid = null;
            int banCount = Main.getSQL().countData("SELECT count(*) FROM ban_data where pid = '" + pid + "';");
            int muteCount = Main.getSQL().countData("SELECT count(*) FROM mute_data where pid = '" + pid + "';");
            int kickCount = Main.getSQL().countData("SELECT count(*) FROM kick_data where pid = '" + pid + "';");
            int warnCount = Main.getSQL().countData("SELECT count(*) FROM warn_data where pid = '" + pid + "';");
            int totalCount = banCount + muteCount + kickCount + warnCount;
            int pages = totalCount % 8 == 0 ? totalCount / 8 : totalCount / 8 + 1;
        });
    }
    
}
