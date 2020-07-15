package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Lightning extends CommandManager {

    public Lightning() {
        super("lightning", "闪电", "/lightning", "shock");
    }

    @Cmd(coolDown = 1500, perm = "onyx.command.lightning", permMessage = "§c你需要 ADMIN §c及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void lightning(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.getWorld().strikeLightningEffect(p.getTargetBlock(null, 200).getLocation());
        p.sendMessage("§a轰！");
    }

}
