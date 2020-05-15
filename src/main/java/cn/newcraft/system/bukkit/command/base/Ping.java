package cn.newcraft.system.bukkit.command.base;

import cn.newcraft.system.bukkit.command.CommandManager;
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
        p.sendMessage("§6你当前的延迟是 §a§l" + ms + "ms");
    }


    @Cmd(arg = "<player>", perm = "ncs.command.ping.other")
    public void pingOther(CommandSender sender, String[] args){
        Player target = Bukkit.getPlayer(args[0]);
        int ms = pingPlayer(target);
        sender.sendMessage("§6玩家 " + target.getDisplayName() + " 的延迟是 §a§l" + ms + "ms");
    }

    private int pingPlayer(Player p) {
        try {
            String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle", new Class[0]).invoke(p);
            Integer a = (Integer)handle.getClass().getDeclaredField("ping").get(handle);
            return a;
        }
        catch (ClassNotFoundException|IllegalAccessException|IllegalArgumentException| InvocationTargetException |NoSuchMethodException|SecurityException|NoSuchFieldException ignored) {}
        return -1;
    }
}
