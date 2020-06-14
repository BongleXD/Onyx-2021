package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Freeze extends CommandManager implements Listener {

    private List<UUID> players = new ArrayList<>();

    public Freeze() {
        super("freeze", "冻结", "/freeze <玩家>", "冻结");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Cmd(arg = "<player>", perm = "ncs.command.freeze", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用！")
    public void freeze(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        if(!p.hasPermission("ncs.command.freeze.bypass")) {
            if(!players.contains(p.getUniqueId())) {
                sender.sendMessage("§a目标 §e" + p.getDisplayName() + " §a已被成功冻结！");
                players.add(p.getUniqueId());
            }else{
                sender.sendMessage("§a目标 §e" + p.getDisplayName() + " §a已被成功解冻！");
                players.remove(p.getUniqueId());
            }
        }else{
            sender.sendMessage("§c目标 §e" + p.getDisplayName() + " §c无法被冻结！");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(players.contains(p.getUniqueId())){
            if(!p.isFlying()){
                p.setAllowFlight(true);
                p.setFlying(true);
            }
            p.sendMessage("");
            p.sendMessage("§c你目前进入了冻结状态！请不要尝试退出当前服务器， 否则你将会被永久移除服务器！");
            p.sendMessage("");
            e.setTo(e.getFrom());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        Player p = e.getPlayer();
        if(players.contains(p.getUniqueId())){
            p.sendMessage("");
            p.sendMessage("§c你目前进入了冻结状态！请不要执行任何指令！");
            p.sendMessage("");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLoggout(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(players.contains(p.getUniqueId())){
            p.setAllowFlight(false);
            p.setFlying(false);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + e.getPlayer().getName() + " 冻结状态下离开服务器");
        }
    }

}
