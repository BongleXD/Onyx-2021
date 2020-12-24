package net.blastmc.onyx.bukkit.command.base;

import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.config.SpawnConfig;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn extends CommandManager {

    public Spawn() {
        super("spawn", "返回出生点", "/setspawn", "onyx.command.spawn");
    }

    @Cmd(perm = "onyx.command.spawn", only = CommandManager.CommandOnly.PLAYER)
    public void spawn(CommandSender sender, String[] args){
        Player p = (Player) sender;
        try {
            p.teleport(SpawnConfig.SPAWN_LOC);
            p.sendMessage("§a已将你传送至出生点！");
        }catch (Exception ex){
            p.sendMessage("§c出生点未设置！");
        }
    }


}
