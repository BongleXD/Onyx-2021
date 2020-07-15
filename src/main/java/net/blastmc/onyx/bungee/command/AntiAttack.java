package net.blastmc.onyx.bungee.command;

import net.blastmc.onyx.bungee.config.DataConfig;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class AntiAttack extends Command {

    public AntiAttack() {
        super("antiattack-bungee", "onyx.command.antiattack", "antiattack", "aat");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 3){
            if (args[0].equalsIgnoreCase("blacklist") || args[0].equalsIgnoreCase("black")){
                if (args[1].equalsIgnoreCase("add")) {
                    DataConfig.cfg.addIP(args[2]);
                    sender.sendMessage("§a已成功将IP " + args[2] + " 加入至黑名单！");
                    return;
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    DataConfig.cfg.removeIP(args[2]);
                    sender.sendMessage("§a已成功将IP " + args[2] + " 移除出黑名单！");
                    return;
                }
                sender.sendMessage("§c执行命令失败，参数错误！");
                return;
            }
            if (args[0].equalsIgnoreCase("whitelist") || args[0].equalsIgnoreCase("white")){
                if (args[1].equalsIgnoreCase("add")) {
                    DataConfig.cfg.addWhitePlayers(args[2]);
                    sender.sendMessage("§a已成功将玩家 " + args[2] + " 加入至白名单！");
                    return;
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    DataConfig.cfg.removeWhitePlayers(args[2]);
                    sender.sendMessage("§a已成功将玩家 " + args[2] + " 移除出白名单！");
                    return;
                }
                sender.sendMessage("§c执行命令失败，参数错误！");
                return;
            }
            sender.sendMessage("§c执行命令失败，参数错误！");
        } else {
            sender.sendMessage("§c用法： /antiattack <blacklist/whitelist> <add/remove> <IP 地址/玩家名>");
        }
    }
}
