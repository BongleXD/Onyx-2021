package cn.newcraft.system.bukkit.command.base;

import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.util.ReflectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class Ping extends CommandManager {

    public Ping() {
        super("ping", "显示自己/他人的延迟", "/ping [玩家]", "延迟", "ms");
    }

    @Cmd(perm = "ncs.command.ping", only = CommandOnly.PLAYER)
    public void ping(CommandSender sender, String[] args){
        Player p = (Player) sender;
        int ms = pingPlayer(p);
        String color = "§a";
        if(ms <= 150 && ms > 100){
            color = "§e";
        }else if(ms > 150){
            color = "§c";
        }
        p.sendMessage("§e你当前的延迟是 " + color + ms + " 毫秒");
    }


    @Cmd(arg = "<player>", perm = "ncs.command.ping.other")
    public void pingOther(CommandSender sender, String[] args){
        Player target = Bukkit.getPlayer(args[0]);
        int ms = pingPlayer(target);
        String color = "§a";
        if(ms <= 150 && ms > 100){
            color = "§e";
        }else if(ms > 150){
            color = "§c";
        }
        sender.sendMessage("§e玩家 " + target.getDisplayName() + " §e的延迟是 " + color + ms + " 毫秒");
    }

    private int pingPlayer(Player p) {
        try {
            Object entityPlayer = ReflectUtils.getCraftClass("entity.CraftPlayer").getMethod("getHandle", new Class[0]).invoke(p);
            int ping = (int) entityPlayer.getClass().getDeclaredField("ping").get(entityPlayer);
            return ping;
        }
        catch (Exception ex) {
            return -1;
        }
    }

}
