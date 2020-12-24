package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WalkSpeed extends CommandManager {

    public WalkSpeed() {
        super("walkspeed", "行走速度", "/walkspeed [玩家] <1-10>", "onyx.command.walk-speed","行走速度");
    }

    @Cmd(perm = "onyx.command.walk-speed", permMessage = "§c你需要 §2MOD §c及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void reset(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.setFlySpeed((float) 0.2);
        p.sendMessage("§a已将你的行走速度修改为默认！");
    }

    @Cmd(arg = "<integer>", perm = "onyx.command.walk-speed", permMessage = "§c你需要 §2MOD §c及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void set(CommandSender sender, String[] args){
        Player p = (Player) sender;
        float f = Float.parseFloat(args[0] + "f") / 10;
        if(f <= 1 && f >= 0) {
            p.setWalkSpeed(f);
            p.sendMessage("§a已将你的行走速度修改为 " + args[0] + "！");
        }else{
            p.sendMessage("§c你输入的数值过大或过小！请控制在 [1-10] 之间");
        }
    }

    @Cmd(arg = "<player> <integer>", perm = "onyx.command.walk-speed", permMessage = "§c你需要 §2MOD §c及以上的会员等级才能使用此指令！")
    public void setOther(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        float f = Float.parseFloat(args[1] + "f") / 10;
        if(f <= 1 && f >= 0) {
            p.setWalkSpeed(f);
            sender.sendMessage("§a已将 " + p.getDisplayName() + " §a的行走速度修改为 " + args[1] + "！");
            if(p != sender) {
                p.sendMessage("§a已将你的行走速度修改为 " + args[1] + "！");
            }
        }else{
            sender.sendMessage("§c你输入的数值过大或过小！请控制在 [1-10] 之间");
        }
    }

}
