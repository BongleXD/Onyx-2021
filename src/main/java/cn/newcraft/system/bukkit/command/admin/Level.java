package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Level extends CommandManager {

    public Level() {
        super("level", "更改等级", "/level <addXp/addLevel> <玩家> <数值>");
    }

    @Cmd(arg = "addXp <player> <integer>", perm = "ncs.command.level.addxp", permMessage = "§c你不能这么做！")
    public void addXp(CommandSender sender, String[] args){
        Player p = (Player) sender;
        Player target = Bukkit.getPlayer(args[1]);
        p.sendMessage("§a已给予玩家 " + target.getDisplayName() + " " + args[2] + " §aEXP！");
        PlayerProfile.getDataFromUUID(target.getUniqueId()).addXp(Integer.parseInt(args[2]));
    }

    @Cmd(arg = "addLevel <player> <integer>", perm = "ncs.command.level.addlevel", permMessage = "§c你不能这么做！")
    public void addLevel(CommandSender sender, String[] args){
        Player p = (Player) sender;
        Player target = Bukkit.getPlayer(args[1]);
        p.sendMessage("§a已给予玩家 " + target.getDisplayName() + " " + args[2] + " §a等级！");
        PlayerProfile.getDataFromUUID(target.getUniqueId()).addLevel(Integer.parseInt(args[2]));
    }

}
