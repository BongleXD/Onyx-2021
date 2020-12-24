package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kaboom extends CommandManager implements Listener {

    private List<UUID> players = new ArrayList<>();

    public Kaboom() {
        super("kaboom", "击飞玩家", "/kaboom [玩家]", "onyx.command.kaboom", "kab", "喀嘣");
        this.setPermissionMessage("§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if(players.contains(e.getEntity().getUniqueId())){
                    e.setCancelled(true);
                    players.remove(e.getEntity().getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onLoggout(PlayerQuitEvent e){
        players.remove(e.getPlayer().getUniqueId());
    }

    @Cmd(perm = "onyx.command.kaboom", permMessage = "§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！")
    public void all(CommandSender sender, String[] args){
        for(Player online : Bukkit.getOnlinePlayers()){
            Bukkit.broadcastMessage("§e" + online.getDisplayName() + " §a已被击飞！");
            online.getWorld().strikeLightningEffect(online.getLocation());
            online.setVelocity(online.getLocation().getDirection().multiply(0).setY(4));
            if(!players.contains(online.getUniqueId())){
                players.add(online.getUniqueId());
            }
        }
    }

    @Cmd(arg = "<player>", perm = "onyx.command.kaboom", permMessage = "§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！")
    public void player(CommandSender sender, String[] args) {
        Player p = Bukkit.getPlayer(args[0]);
        Bukkit.broadcastMessage("§e" + p.getDisplayName() + " §a已被击飞！");
        p.getWorld().strikeLightningEffect(p.getLocation());
        p.setVelocity(p.getLocation().getDirection().multiply(0).setY(4));
        if(!players.contains(p.getUniqueId())){
            players.add(p.getUniqueId());
        }
    }

}
