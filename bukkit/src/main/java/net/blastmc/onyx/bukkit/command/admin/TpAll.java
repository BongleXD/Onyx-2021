package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAll extends CommandManager {

    public TpAll() {
        super("tpall", "传送全体", "/tpall", "传送全体");
    }

    @Cmd(perm = "onyx.command.tpall", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void tpall(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.sendMessage("§a已将所有玩家传送至你的位置！");
        for(Player online : Bukkit.getOnlinePlayers()){
            if(p == online){
                continue;
            }
            online.teleport(p);
        }
    }

}