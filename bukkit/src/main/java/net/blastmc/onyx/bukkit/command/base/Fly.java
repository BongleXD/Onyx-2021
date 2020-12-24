package net.blastmc.onyx.bukkit.command.base;

import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly extends CommandManager {

    public Fly() {
        super("fly", "飞行", "/fly [玩家] [on/off]", "onyx.command.fly", "飞行");
        this.setPermissionMessage("§c你需要 §aVIP §c及以上的会员等级才能使用飞行！ 请移步至主大厅进行购买会员等级！");
    }

    @Cmd(perm = "onyx.command.fly", permMessage = "§c你需要 §aVIP §c及以上的会员等级才能使用飞行！ 请移步至主大厅进行购买会员等级！", only = CommandOnly.PLAYER)
    public void fly(CommandSender sender, String[] args){
        Player p = (Player) sender;
        boolean b = p.getAllowFlight();
        p.setAllowFlight(!b);
        p.setFlying(!b);
        p.sendMessage(b ? "§c飞行已关闭！" : "§a飞行已开启！");
    }

    @Cmd(arg = "on", aliases = "开启", perm = "lynx.command.fly.force", permMessage = "§c你需要 §aVIP §c及以上的会员等级才能使用飞行！ 请移步至主大厅进行购买会员等级！", only = CommandOnly.PLAYER)
    public void flyOn(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.setAllowFlight(true);
        p.setFlying(true);
        p.sendMessage("§a飞行已开启！");
    }

    @Cmd(arg = "off", aliases = "关闭", perm = "lynx.command.fly.force", permMessage = "§c你需要 §aVIP §c及以上的会员等级才能使用飞行！ 请移步至主大厅进行购买会员等级！", only = CommandOnly.PLAYER)
    public void flyOff(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.setAllowFlight(false);
        p.setFlying(false);
        p.sendMessage("§c飞行已关闭！");
    }

    @Cmd(arg = "<player>", perm = "lynx.command.fly.other", permMessage = "§c你需要 §9HELPER §c及以上的会员等级才能使用此指令！")
    public void flyOther(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        boolean b = p.getAllowFlight();
        p.setAllowFlight(!b);
        p.setFlying(!b);
        sender.sendMessage(b ? "§c" + p.getDisplayName() + " 的飞行已关闭！" : "§a" + p.getDisplayName() + " 的飞行已开启！");
        if(p != sender) {
            p.sendMessage(b ? "§c飞行已关闭！" : "§a飞行已开启！");
        }
    }

    @Cmd(arg = "<player> on", aliases = "<player> 开启", perm = "lynx.command.fly.other.force", permMessage = "§c你需要 §9HELPER §c及以上的会员等级才能使用此指令！")
    public void flyOtherOn(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        p.setAllowFlight(true);
        p.setFlying(true);
        sender.sendMessage("§a" + p.getDisplayName() + " §a的飞行已开启！");
        if(p != sender) {
            p.sendMessage("§a飞行已开启！");
        }
    }

    @Cmd(arg = "<player> off", aliases = "<player> 关闭", perm = "lynx.command.fly.other.force", permMessage = "§c你需要 §9HELPER §c及以上的会员等级才能使用此指令！")
    public void flyOtherOff(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        p.setAllowFlight(false);
        p.setFlying(false);
        sender.sendMessage("§c" + p.getDisplayName() + " §a的飞行已关闭！");
        if(p != sender) {
            p.sendMessage("§c飞行已关闭！");
        }
    }

}
