package cn.newcraft.system.bukkit.command.base;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.config.SpawnConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn extends CommandManager{

    public Spawn() {
        super("spawn", "返回出生点", "/setspawn");
    }

    @Cmd(perm = "ncs.command.spawn", only = CommandManager.CommandOnly.PLAYER)
    public void spawn(CommandSender sender, String[] args){
        Player p = (Player) sender;
        try {
            p.teleport((Location) SpawnConfig.cfg.getYml().get("spawn.loc"));
            p.sendMessage("§a已将你传送至出生点！");
        }catch (Exception ex){
            p.sendMessage("§c出生点未设置！");
        }
    }


}
