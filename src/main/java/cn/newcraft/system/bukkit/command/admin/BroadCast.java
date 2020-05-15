package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.util.Method;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class BroadCast extends CommandManager {

    public BroadCast() {
        super("broadcast", "全服通知", "/broadcast [all] <信息>", "广播", "bc");
    }

    @Cmd(arg = "<value...>", perm = "ncs.command.broadcast", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void broadcast(CommandSender sender, String[] args){
        Bukkit.broadcastMessage("§6➤§r " + Method.transColor(Arrays.toString(args).replace("[", "").replace("]", "").replace(",", "")));
    }

    @Cmd(arg = "all <value...>", perm = "ncs.command.broadcast.all", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void broadcastAll(CommandSender sender, String[] args){
        sender.sendMessage("§c开发中...");
    }

}
