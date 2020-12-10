package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.config.SpawnConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SetSpawn extends CommandManager implements Listener{

    public SetSpawn() {
        super("setspawn", "设置出生点", "/setspawn");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Cmd(perm = "onyx.command.setspawn", only = CommandOnly.PLAYER)
    public void setSpawn(CommandSender sender, String[] args){
        Player p = (Player) sender;
        SpawnConfig.cfg.getYml().set("spawn.loc", p.getLocation());
        SpawnConfig.cfg.save();
        p.sendMessage("§a出生点已成功设置！");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        try {
            if (SpawnConfig.cfg.getYml().get("spawn.loc") != null) {
                e.getPlayer().teleport((Location) SpawnConfig.cfg.getYml().get("spawn.loc"));
            }
        }catch (ClassCastException ignored){}
    }


}
