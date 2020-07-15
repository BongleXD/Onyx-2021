package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.util.Method;
import net.blastmc.onyx.bukkit.command.CommandManager;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Echo extends CommandManager {

    public Echo() {
        super("echo", "通知某人", "/echo <玩家> <信息>", "通知");
    }

    @Cmd(arg = "<player> <value...>", perm = "onyx.command.broadcast", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void echo(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        List<String> list = Lists.newArrayList();
        for(int i = 0; i < args.length; i++){
            if(i == 0){
                continue;
            }
            list.add(args[i]);
        }
        p.sendMessage(Method.transColor(Joiner.on(" ").join(list)));
    }

}
