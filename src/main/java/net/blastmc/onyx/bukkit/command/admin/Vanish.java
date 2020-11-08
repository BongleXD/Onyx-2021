package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.api.PlayerProfile;
import net.blastmc.onyx.bukkit.util.BukkitMethod;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Vanish extends CommandManager implements Listener {

    public Vanish() {
        super("vanish", "隐身", "/vanish [玩家] [on/off]", "v", "隐身");
    }

    @Cmd(coolDown = 5000, perm = "onyx.command.vanish", permMessage = "§c你不能这么做！", only = CommandOnly.PLAYER)
    public void vanish(CommandSender sender, String[] args){
        Player p = (Player) sender;
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        for(Player online : Bukkit.getOnlinePlayers()){
            BukkitMethod.vanishPlayer(p, online, !prof.isVanish());
        }
        prof.setVanish(!prof.isVanish());
        p.sendMessage(prof.isVanish() ? "§a你隐身了！" : "§c你解除了隐身！");
        if(PlayerProfile.getVanishs().contains(prof)){
            PlayerProfile.getVanishs().remove(prof);
        }else{
            PlayerProfile.getVanishs().add(prof);
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> prof.saveData(false));
    }

    

}
