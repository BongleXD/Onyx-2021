package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Crash extends CommandManager {

    public Crash() {
        super("crash", "崩溃玩家客户端", "/crash <玩家>", "onyx.command.crash","byehacker");
    }

    @Cmd(arg = "<player>", perm = "onyx.command.crash", permMessage = "你不能这么做！")
    public void crash(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        sender.sendMessage(p.getDisplayName() + " §a已被崩溃客户端！");
        Main.getNMS().crashClient(p);
    }

}
