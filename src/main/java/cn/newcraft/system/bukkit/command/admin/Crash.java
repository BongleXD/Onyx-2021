package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Crash extends CommandManager {

    public Crash() {
        super("crash", "崩溃玩家客户端", "/crash <玩家>", "byehacker");
    }

    @Cmd(arg = "<player>", perm = "ncs.command.crash", permMessage = "你不能这么做！")
    public void crash(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        sender.sendMessage(p.getDisplayName() + " §a已被崩溃客户端！");
        Main.getNMS().crashClient(p);
    }

}
