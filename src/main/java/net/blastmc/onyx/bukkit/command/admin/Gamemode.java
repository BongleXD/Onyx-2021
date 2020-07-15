package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Gamemode extends CommandManager {

    public Gamemode() {
        super("gamemode", "调整游戏模式", "/gamemode [玩家] <模式>", "gm", "模式");
        this.setPermissionMessage("§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！");
    }

    @Cmd(arg = "<value>", perm = "onyx.command.gamemode", permMessage = "§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void mode(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("生存") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s")) {
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage("§a已将你的游戏模式调整为§e生存模式§a！");
        }else if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("创造") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c")) {
            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage("§a已将你的游戏模式调整为§e创造模式§a！");
        }else if(args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("冒险") || args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a")) {
            p.setGameMode(GameMode.ADVENTURE);
            p.sendMessage("§a已将你的游戏模式调整为§e冒险模式§a！");
        }else if(args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("观察者") || args[0].startsWith("sp")) {
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage("§a已将你的游戏模式调整为§e观察者模式§a！");
        }else{
            p.sendMessage("§c用法： /gamemode [玩家] <模式>");
        }
    }

    @Cmd(arg = "<value> <player>", perm = "onyx.command.gamemode.force", permMessage = "§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！")
    public void modeOther(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[1]);
        if(args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("生存") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s")) {
            p.setGameMode(GameMode.SURVIVAL);
            sender.sendMessage("§a已将 " + p.getDisplayName() + " 的游戏模式调整为§e生存模式§a！");
            if(p != sender) {
                p.sendMessage("§a已将你的游戏模式调整为§e生存模式§a！");
            }
        }else if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("创造") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c")) {
            p.setGameMode(GameMode.CREATIVE);
            sender.sendMessage("§a已将 " + p.getDisplayName() + " 的游戏模式调整为§e创造模式§a！");
            if(p != sender) {
                p.sendMessage("§a已将你的游戏模式调整为§e创造模式§a！");
            }
        }else if(args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("冒险") || args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a")) {
            p.setGameMode(GameMode.ADVENTURE);
            sender.sendMessage("§a已将 " + p.getDisplayName() + " 的游戏模式调整为§e冒险模式§a！");
            if(p != sender) {
                p.sendMessage("§a已将你的游戏模式调整为§e冒险模式§a！");
            }
        }else if(args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("观察者") || args[0].startsWith("sp")) {
            p.setGameMode(GameMode.SPECTATOR);
            sender.sendMessage("§a已将 " + p.getDisplayName() + " 的游戏模式调整为§e观察者模式§a！");
            if(p != sender) {
                p.sendMessage("§a已将你的游戏模式调整为§e观察者模式§a！");
            }
        }else{
            p.sendMessage("§c用法： /gamemode [玩家] <模式>");
        }
    }

}
