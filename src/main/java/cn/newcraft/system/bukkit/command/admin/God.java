package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class God extends CommandManager implements Listener {

    private List<UUID> players = new ArrayList<>();

    public God() {
        super("godmode", "无敌模式", "/godmode [玩家] [on/off]", "无敌", "god");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Cmd(perm = "ncs.command.god", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void god(CommandSender sender, String[] args){
        Player p = (Player) sender;
        boolean b = this.players.contains(p.getUniqueId());
        p.sendMessage(b ? "§c无敌模式已关闭！" : "§a无敌模式已开启！");
        if (b) {
            this.players.remove(p.getUniqueId());
        } else {
            this.players.add(p.getUniqueId());
        }
    }

    @Cmd(arg = "on", perm = "ncs.command.god.force", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void godOn(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.sendMessage("§a无敌模式已开启！");
        if(!players.contains(p.getUniqueId())) {
            players.add(p.getUniqueId());
        }
    }

    @Cmd(arg = "off", perm = "ncs.command.god.force", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void godOff(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.sendMessage("§c无敌模式已关闭！");
        players.remove(p.getUniqueId());
    }

    @Cmd(arg = "<player>", perm = "ncs.command.god.other.force", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void godOther(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        boolean b = p.getNoDamageTicks() == -1;
        p.sendMessage(b ? "§c无敌模式已关闭！" : "§a无敌模式已开启！");
        if (b) {
            players.remove(p.getUniqueId());
        } else {
            players.add(p.getUniqueId());
        }
        if(p != sender){
            sender.sendMessage(b ? "§c" + p.getDisplayName() + " §c的无敌模式已关闭!" : "§a" + p.getDisplayName() + " §a的无敌模式已开启!");
        }
    }

    @Cmd(arg = "<player> on", perm = "ncs.command.god.other.force", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void godOtherOn(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        p.sendMessage("§a无敌模式已开启！");
        if(!players.contains(p.getUniqueId())) {
            players.add(p.getUniqueId());
        }
        if(p != sender){
            sender.sendMessage("§a" + p.getDisplayName() + " §a的无敌模式已开启!");
        }
    }

    @Cmd(arg = "<player> off", perm = "ncs.command.god.other.force", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void godOtherOff(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        p.sendMessage("§c无敌模式已关闭！");
        players.remove(p.getUniqueId());
        if(p != sender){
            sender.sendMessage("§c" + p.getDisplayName() + " §a的无敌模式已关闭!");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (players.contains(p.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }
}
