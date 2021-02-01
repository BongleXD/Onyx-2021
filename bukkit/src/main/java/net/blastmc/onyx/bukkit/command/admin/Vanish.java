package net.blastmc.onyx.bukkit.command.admin;

import com.google.common.collect.Lists;
import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.utils.Method;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public class Vanish extends CommandManager implements Listener {

    public static List<PlayerProfile> vanishList = Lists.newArrayList();

    public Vanish() {
        super("vanish", "隐身", "/vanish [玩家] [on/off]", "onyx.command.vanish", "v", "隐身");
    }

    @Cmd(coolDown = 5000, perm = "onyx.command.vanish", permMessage = "§c你不能这么做！", only = CommandOnly.PLAYER)
    public void vanish(CommandSender sender, String[] args){
        Player p = (Player) sender;
        PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
        for(Player online : Bukkit.getOnlinePlayers()){
            Method.vanishPlayer(p, online, !prof.isVanish());
        }
        prof.setVanish(!prof.isVanish());
        p.sendMessage(prof.isVanish() ? "§a你隐身了！" : "§c你解除了隐身！");
        if(vanishList.contains(prof)){
            vanishList.remove(prof);
        }else{
            vanishList.add(prof);
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> prof.saveData(false));
    }

    

}
