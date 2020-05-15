package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Lightning extends CommandManager {

    public Lightning() {
        super("lightning", "闪电", "/lightning", "shock");
    }

    @Cmd(coolDown = 1500, perm = "ncs.command.lightning", permMessage = "§c你需要 ADMIN §c及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void lightning(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.getWorld().strikeLightningEffect(p.getTargetBlock(null, 200).getLocation());
        p.sendMessage("§a轰！");
    }

}
