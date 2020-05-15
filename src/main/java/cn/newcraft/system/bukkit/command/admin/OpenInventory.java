package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenInventory extends CommandManager {

    public OpenInventory() {
        super("openinventory", "打开一个玩家的背包", "/openinventory <玩家>", "openinv", "invsee");
    }

    @Cmd(arg = "<player>", perm = "ncs.command.openinventory", permMessage = "§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void inventory(CommandSender sender, String[] args){
        Player p = (Player)sender;
        Player target = Bukkit.getPlayer(args[0]);
        p.openInventory(target.getInventory());
    }
}
