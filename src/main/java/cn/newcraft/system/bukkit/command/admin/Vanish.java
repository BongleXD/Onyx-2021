package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import static cn.newcraft.system.bukkit.util.Method.vanishPlayer;

public class Vanish extends CommandManager implements Listener {

    public Vanish() {
        super("vanish", "隐身", "/vanish [玩家] [on/off]", "v", "隐身");
    }

    @Cmd(coolDown = 5000, perm = "ncs.command.vanish", permMessage = "§c你不能这么做！", only = CommandOnly.PLAYER)
    public void vanish(CommandSender sender, String[] args){
        Player p = (Player) sender;
        PlayerProfile profile = PlayerProfile.getDataFromUUID(p.getUniqueId());
        for(Player online : Bukkit.getOnlinePlayers()){
            vanishPlayer(p, online, !profile.isVanish());
        }
        profile.setVanish(!profile.isVanish());
        if(PlayerProfile.vanishs.contains(p.getUniqueId())){
            PlayerProfile.vanishs.remove(p.getUniqueId());
        }else{
            PlayerProfile.vanishs.add(p.getUniqueId());
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                profile.saveData(false);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    

}
