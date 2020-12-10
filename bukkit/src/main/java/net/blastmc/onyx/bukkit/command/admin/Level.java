package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Level extends CommandManager {

    public Level() {
        super("level", "更改等级", "/level <addXp/addLevel> <玩家> <数值>");
    }

    @Cmd(arg = "addXp <player> <integer>", perm = "onyx.command.level.addxp", permMessage = "§c你不能这么做！")
    public void addXp(CommandSender sender, String[] args){
        Player target = Bukkit.getPlayer(args[1]);
        PlayerProfile prof = Onyx.getPlayerProfile(target.getUniqueId());
        if(prof == null){
            sender.sendMessage("§c玩家 " + target.getDisplayName() + " §c没有档案数据！");
            return;
        }
        sender.sendMessage("§a已给予玩家 " + target.getDisplayName() + " " + args[2] + " §aEXP！");
        prof.addXpWithoutCallEvent(Integer.parseInt(args[2]));
    }

    @Cmd(arg = "addLevel <player> <integer>", perm = "onyx.command.level.addlevel", permMessage = "§c你不能这么做！")
    public void addLevel(CommandSender sender, String[] args){
        Player target = Bukkit.getPlayer(args[1]);
        PlayerProfile prof = Onyx.getPlayerProfile(target.getUniqueId());
        if(prof == null){
            sender.sendMessage("§c玩家 " + target.getDisplayName() + " §c没有档案数据！");
            return;
        }
        sender.sendMessage("§a已给予玩家 " + target.getDisplayName() + " " + args[2] + " §a等级！");
        prof.addLevel(Integer.parseInt(args[2]));
    }

}
