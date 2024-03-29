package net.blastmc.onyx.survival.command.base;

import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.survival.Main;
import net.blastmc.onyx.survival.command.TeleportMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Home extends CommandManager implements Listener {

    public Home() {
        super("home", "返回家", "/home <家名字>", "survival.command.home");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Cmd(arg = "<value>", perm = "survival.command.home", coolDown = 3000, only = CommandOnly.PLAYER)
    public void home(CommandSender sender, String[] args) throws SQLException {
        Player p = (Player) sender;
        String name = args[0];
        if (TeleportMap.getTpQueue().containsKey(p.getUniqueId())) {
            p.sendMessage("§c你当前已经有个正在进行的传送请求！");
            return;
        }
        Connection conn = Main.getSql().getConnection();
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM player_home WHERE uuid='" + p.getUniqueId().toString() + "' AND homename LIKE UPPER('" + name + "');");
        if (result.next()) {
            String homeName = result.getString(3);
            World world = Bukkit.getServer().getWorld(result.getString(4));
            double x = result.getDouble(5);
            double y = result.getDouble(6);
            double z = result.getDouble(7);
            p.sendMessage("§a即将在 §e3 §a秒后传送至家 §e" + homeName + " §a请不要移动！");
            BukkitTask task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (TeleportMap.getTpQueue().containsKey(p.getUniqueId())) {
                    TeleportMap.getTpQueue().remove(p.getUniqueId());
                    Location loc = new Location(world, x, y, z);
                    p.teleport(loc);
                    p.sendMessage("§a已将你传送至家 §b" + homeName);
                }
            }, 60L);
            TeleportMap.getTpQueue().put(p.getUniqueId(), task);
        } else {
            p.sendMessage("§c家 " + name + " 不存在！");
        }
        result.close();
        statement.close();
        conn.close();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY() && e.getTo().getBlockZ() == e.getFrom().getBlockZ()) {
            return;
        }
        if (TeleportMap.getTpQueue().containsKey(e.getPlayer().getUniqueId())) {
            TeleportMap.getTpQueue().remove(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage("§c你在即将传送时移动了，传送取消！");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        TeleportMap.getTpQueue().remove(e.getPlayer().getUniqueId());
    }

}
