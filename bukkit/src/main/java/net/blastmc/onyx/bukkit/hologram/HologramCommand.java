package net.blastmc.onyx.bukkit.hologram;

import net.blastmc.onyx.api.bukkit.Hologram;
import net.blastmc.onyx.api.bukkit.event.PlayerInitEvent;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.config.HologramConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class HologramCommand extends CommandManager implements Listener {

    public HologramCommand() {
        super("hologram", "全体图", "/hologram help 查看帮助", "onyx.command.hologram", "holo", "hd", "hg");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            HologramConfig.holoMap.values().forEach(Hologram::show);
        }, 20L, 20L);
    }

    @EventHandler
    public void onJoin(PlayerInitEvent e){
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            HologramConfig.holoMap.forEach((name, holo) -> {
                holo.showTo(e.getPlayer());
                holo.show();
            });
        }, 20L);
    }

    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent e){
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            HologramConfig.holoMap.forEach((name, holo) -> {
                holo.showTo(e.getPlayer());
                holo.show();
            });
        }, 20L);
    }

    @Cmd(arg = "create <value>", perm = "onyx.command.hologram", only = CommandOnly.PLAYER)
    public void create(CommandSender sender, String[] args){
        Player p = (Player) sender;
        String name = args[1];
        if(HologramConfig.holoMap.containsKey(name)){
            sender.sendMessage("§c全息图 §e" + name + " §c已存在！ 请到 §bhologram.yml §c编辑相关数据！");
            return;
        }
        Hologram holo = Main.getNMS().newInstance(p.getEyeLocation());
        HologramConfig.holoMap.put(name, holo);
        HologramConfig.config.save();
        sender.sendMessage("§a全息图 §e" + name + " §a已创建！");
    }

    @Cmd(arg = "tphere <value>", perm = "onyx.command.hologram", only = CommandOnly.PLAYER)
    public void tphere(CommandSender sender, String[] args){
        Player p = (Player) sender;
        String name = args[1];
        Hologram holo = HologramConfig.holoMap.getOrDefault(name, null);
        if(holo == null){
            sender.sendMessage("§c全息图 §e" + name + " §不存在！");
            return;
        }
        holo.location(p.getEyeLocation()).show();
        HologramConfig.config.save();
        sender.sendMessage("§a全息图 §e" + name + " §a已传送至你的所在地！");
    }

}
