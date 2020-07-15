package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpHere extends CommandManager {

    public TpHere() {
        super("tphere", "传送此地", "/tphere <玩家>");
    }

    @Cmd(arg = "<player>", perm = "onyx.command.tphere", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令", only = CommandOnly.PLAYER)
    public void tphere(CommandSender sender, String[] args){
        Player p = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if(p != target) {
            target.teleport(p);
            p.sendMessage("§a已将 " + target.getDisplayName() + " §a传送至你目前所在地！");
            target.sendMessage("§a已将你传送至 " + p.getDisplayName());
        }else{
            p.sendMessage("§c你不能自己传送自己！");
        }
    }

}
