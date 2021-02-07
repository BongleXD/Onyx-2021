package net.blastmc.onyx.survival.command.base;

import com.google.common.collect.Lists;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.survival.Main;
import net.blastmc.onyx.survival.command.TeleportMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Back extends CommandManager implements Listener {

    private Map<UUID, Location> deathMap = new HashMap<>();
    private Map<UUID, Location> onGroundMap = new HashMap<>();

    public Back() {
        super("back", "返回上一个死亡点", "/back", "survival.command.back");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Cmd(coolDown = 3000, perm = "survival.command.back", only = CommandOnly.PLAYER)
    public void back(CommandSender sender, String[] args){
        Player p = (Player)sender;
        if (deathMap.containsKey(p.getUniqueId())){
            p.sendMessage("§a即将在 §e3 §a秒后传送至死亡点 §c请不要移动！");
            BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (TeleportMap.getTpQueue().containsKey(p.getUniqueId())){
                    p.teleport(deathMap.get(p.getUniqueId()));
                    p.sendMessage("§a已将你传送至死亡点！");
                    deathMap.remove(p.getUniqueId());
                }
            }, 60L);
            TeleportMap.getTpQueue().put(p.getUniqueId(), task);
        } else {
            p.sendMessage("§c找不到死亡点，该死亡点可能已被传送过一次或不存在！");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("survival.command.back") && p.isOnGround() && p.getLocation().getBlock().getRelative(BlockFace.UP).getType() != Material.LAVA) {
            onGroundMap.put(p.getUniqueId(), p.getLocation());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        if (p.hasPermission("survival.command.back")){
            deathMap.put(p.getUniqueId(), onGroundMap.get(p.getUniqueId()));
            p.sendMessage("§e输入 §c/back §e返回死亡点！");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        deathMap.remove(e.getPlayer().getUniqueId());
        onGroundMap.remove(e.getPlayer().getUniqueId());
    }
}
