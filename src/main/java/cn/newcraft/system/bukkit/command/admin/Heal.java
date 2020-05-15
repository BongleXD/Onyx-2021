package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Heal extends CommandManager {

    public Heal() {
        super("heal", "治疗", "/heal [玩家]", "治疗");
    }

    @Cmd(perm = "ncs.command.heal", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void heal(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.sendMessage("§e你已被治疗！");
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(40);
    }

    @Cmd(arg = "<player>", perm = "ncs.command.heal.other", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void healOther(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        sender.sendMessage("§a玩家 " + p.getDisplayName() + " §a已被治疗！");
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(40);
        if(p != sender){
            p.sendMessage("§e你已被治疗！");
        }
    }
}
