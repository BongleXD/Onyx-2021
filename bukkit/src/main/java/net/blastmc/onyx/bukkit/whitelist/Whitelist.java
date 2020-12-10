package net.blastmc.onyx.bukkit.whitelist;

import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.config.WhitelistConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Whitelist extends CommandManager {

    public Whitelist() {
        super("whitelist", "白名单", "/whitelist <add/remove/reload/on/off> [玩家]", "白名单", "wl");
    }

    @Cmd(arg = "add <value>", perm = "onyx.command.whitelist", permMessage = "§c你需要 OWNER 及以上的会员等级才能使用此指令！")
    public void add(CommandSender sender, String[] args){
        boolean b = WhitelistConfig.addPlayer(args[1]);
        sender.sendMessage(b ? "§a已将玩家 " + args[1] + " 加入至白名单！" : "§c玩家 " + args[1] + " 已经在白名单内！");
    }

    @Cmd(arg = "remove <value>", perm = "onyx.command.whitelist", permMessage = "§c你需要 OWNER 及以上的会员等级才能使用此指令！")
    public void remove(CommandSender sender, String[] args){
        boolean b = WhitelistConfig.removePlayer(args[1]);
        sender.sendMessage(b ? "§a已将玩家 " + args[1] + " 从移除白名单！" : "§c玩家 " + args[1] + " 未在白名单内！");
    }

    @Cmd(arg = "reload", perm = "onyx.command.whitelist", permMessage = "§c你需要 OWNER 及以上的会员等级才能使用此指令！")
    public void reload(CommandSender sender, String[] args){
        WhitelistConfig.cfg.reload();
        sender.sendMessage("§a已从配置文件刷新白名单！");
    }

    @Cmd(arg = "on", perm = "onyx.command.whitelist", permMessage = "§c你需要 OWNER 及以上的会员等级才能使用此指令！")
    public void on(CommandSender sender, String[] args){
        WhitelistConfig.setEnabled(true);
        sender.sendMessage("§a已开启白名单！");
        Bukkit.getOnlinePlayers().forEach(online -> {
            if(!WhitelistConfig.getWhitelist().contains(online.getUniqueId().toString())){
                online.kickPlayer("§c此服务器已开启白名单！你未拥有此服务器的白名单！");
            }
        });
    }

    @Cmd(arg = "off", perm = "onyx.command.whitelist", permMessage = "§c你需要 OWNER 及以上的会员等级才能使用此指令！")
    public void off(CommandSender sender, String[] args){
        WhitelistConfig.setEnabled(false);
        sender.sendMessage("§c已关闭白名单！");
    }

}
