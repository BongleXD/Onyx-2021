package cn.newcraft.system.bukkit.gamemanager;

import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.command.CommandSender;

public class ManagerCommand extends CommandManager {

    public ManagerCommand() {
        super("gamemanager", "游戏管理器", "/gamemanager help", "gm");
    }

    @Cmd(perm = "ncs.command.gamemanager", only = CommandOnly.PLAYER)
    public void gm(CommandSender sender, String[] args){
        sender.sendMessage("§b/gamemanager teams §c查看队伍");
        sender.sendMessage("§b/who §c查看当前游戏情况");
    }

}
