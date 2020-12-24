package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.util.interact.TitleUtil;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Title extends CommandManager {

    public Title() {
        super("title", "发送标题信息", "/title <玩家名/all/me> <标题> [副标题] [渐入] [持续] [渐出]", "onyx.command.title");
    }

    @Cmd(arg = "<value> <value>", perm = "onyx.command.title")
    public void playerTitle(CommandSender sender, String[] args){
        switch (args[0].toLowerCase()){
            case "all":
                for (Player players : Bukkit.getOnlinePlayers()) {
                    TitleUtil.sendTitle(players, 20, 100, 20, args[1].replaceAll("&","§"), "");
                }
                sender.sendMessage("§a标题命令执行成功！");
            case "me":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c该命令不能在控制台执行！");
                    return;
                }
                Player p = (Player) sender;
                TitleUtil.sendTitle(p, 20, 100, 20, args[1].replaceAll("&","§"), "");
                p.sendMessage("§a标题命令执行成功！");
            default:
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null){
                    sender.sendMessage("§c玩家 " + args[0] + " 不存在！");
                }
                TitleUtil.sendTitle(target, 20, 100, 20, args[1].replaceAll("&","§"), "");
                sender.sendMessage("§a标题命令执行成功！");
        }
    }

    @Cmd(arg = "<value> <value> <value>", perm = "onyx.command.title")
    public void playerSubTitle(CommandSender sender, String[] args){
        switch (args[0].toLowerCase()){
            case "all":
                for (Player players : Bukkit.getOnlinePlayers()) {
                    TitleUtil.sendTitle(players, 20, 100, 20, args[1].replaceAll("&","§"), args[2].replaceAll("&","§"));
                }
                sender.sendMessage("§a标题命令执行成功！");
            case "me":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c该命令不能在控制台执行！");
                    return;
                }
                Player p = (Player) sender;
                TitleUtil.sendTitle(p, 20, 100, 20, args[1].replaceAll("&","§"), args[2].replaceAll("&","§"));
                p.sendMessage("§a标题命令执行成功！");
            default:
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null){
                    sender.sendMessage("§c玩家 " + args[0] + " 不存在！");
                }
                TitleUtil.sendTitle(target, 20, 100, 20, args[1].replaceAll("&","§"), args[2].replaceAll("&","§"));
                sender.sendMessage("§a标题命令执行成功！");
        }
    }

    @Cmd(arg = "<value> <value> <integer> <integer> <integer>", perm = "onyx.command.title")
    public void playerIntTitle(CommandSender sender, String[] args){
        switch (args[0].toLowerCase()){
            case "all":
                for (Player players : Bukkit.getOnlinePlayers()) {
                    TitleUtil.sendTitle(players, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[1].replaceAll("&","§"), "");
                }
                sender.sendMessage("§a标题命令执行成功！");
            case "me":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c该命令不能在控制台执行！");
                    return;
                }
                Player p = (Player) sender;
                TitleUtil.sendTitle(p, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[1].replaceAll("&","§"), "");
                p.sendMessage("§a标题命令执行成功！");
            default:
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null){
                    sender.sendMessage("§c玩家 " + args[0] + " 不存在！");
                }
                TitleUtil.sendTitle(target, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[1].replaceAll("&","§"), "");
                sender.sendMessage("§a标题命令执行成功！");
        }
    }

    @Cmd(arg = "<value> <value> <value> <integer> <integer> <integer>", perm = "onyx.command.title")
    public void playerIntSubTitle(CommandSender sender, String[] args){
        switch (args[0].toLowerCase()){
            case "all":
                for (Player players : Bukkit.getOnlinePlayers()) {
                    TitleUtil.sendTitle(players, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), args[1].replaceAll("&","§"), args[2].replaceAll("&","§"));
                }
                sender.sendMessage("§a标题命令执行成功！");
            case "me":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§c该命令不能在控制台执行！");
                    return;
                }
                Player p = (Player) sender;
                TitleUtil.sendTitle(p, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), args[1].replaceAll("&","§"), args[2].replaceAll("&","§"));
                p.sendMessage("§a标题命令执行成功！");
            default:
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null){
                    sender.sendMessage("§c玩家 " + args[0] + " 不存在！");
                }
                TitleUtil.sendTitle(target, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), args[1].replaceAll("&","§"), args[2].replaceAll("&","§"));
                sender.sendMessage("§a标题命令执行成功！");
        }
    }
}
