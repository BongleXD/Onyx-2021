package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.command.CommandManager;
import com.google.common.base.Joiner;
import net.blastmc.onyx.api.util.Method;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class BroadCast extends CommandManager {

    public BroadCast() {
        super("broadcast", "全服通知", "/broadcast [all] <信息>", "onyx.command.broadcast", "广播", "bc");
    }

    @Cmd(arg = "<value...>", perm = "onyx.command.broadcast", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void broadcast(CommandSender sender, String[] args){
        Bukkit.broadcastMessage("§c[广播]§r " + Method.transColor(Joiner.on(" ").join(args)));
    }

}
