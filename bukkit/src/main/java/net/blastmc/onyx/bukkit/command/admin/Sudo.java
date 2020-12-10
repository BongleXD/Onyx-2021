package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sudo extends CommandManager {

    public Sudo() {
        super("sudo", "强行执行指令", "/sudo <玩家> <指令...>", "执行");
        this.setPermissionMessage("§c你需要 ADMIN 及以上的会员等级才能使用此指令！");
    }

    @Cmd(arg = "<player> <value...>", perm = "onyx.command.sudo", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void sudo(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        StringBuilder sb = new StringBuilder();
        for(String s : args){
            if(s.equals(args[0])){
                continue;
            }
            sb.append(s).append(" ");
        }
        Bukkit.dispatchCommand(p, sb.toString());
        sender.sendMessage("§a执行成功！");
    }

}
