package net.blastmc.onyx.bukkit.command.base;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class FootStep extends CommandManager implements Listener {

    private static HashMap<Player, Boolean> taskMap = new HashMap<>();
    private HashMap<Player, Boolean> dataMap = new HashMap<>();
    private HashMap<Player, Long> coolDownMap = new HashMap<>();

    public FootStep() {
        super("footstep", "开启足迹模式", "/footstep [玩家] [on/off]", "onyx.command.footstep", "fs", "fp", "footprint", "step", "足迹模式");
        this.setPermissionMessage("§c你需要 §2MOD §c及以上的会员等级才能使用此指令！");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Cmd(arg = "<player> on", perm = "onyx.command.footstep.force", permMessage = "§c你需要 §2MOD §c及以上的会员等级才能使用此指令！")
    public void forceEnable(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        sender.sendMessage("§a" + p.getDisplayName() + " §a的足迹模式已开启！");
        p.sendMessage("§a足迹模式已开启！ 你的脚印将被显示在服务器内！");
        setCoolDown(p, 0);
        taskMap.put(p, true);
    }

    @Cmd(arg = "<player> off", perm = "onyx.command.footstep.force", permMessage = "§c你需要 §2MOD §c及以上的会员等级才能使用此指令！")
    public void forceDisable(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        sender.sendMessage("§c" + p.getDisplayName() + " §a的足迹模式已关闭！");
        p.sendMessage("§c足迹模式已关闭！ 你的脚印将不会显示在服务器内！");
        taskMap.put(p, false);
    }

    @Cmd(arg = "on", perm = "onyx.command.footstep", permMessage = "§c你需要 §bSVIP §c及以上的会员等级才能使用显示足迹！ 请移步到大厅进行购买会员等级！", only = CommandOnly.PLAYER)
    public void enable(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.sendMessage("§a足迹模式已开启！ 你的脚印将被显示在服务器内！");
        setCoolDown(p, 0);
        taskMap.put(p, true);
    }

    @Cmd(arg = "off", perm = "onyx.command.footstep", permMessage = "§c你需要 §bSVIP §c及以上的会员等级才能使用显示足迹！ 请移步到大厅进行购买会员等级！", only = CommandOnly.PLAYER)
    public void disable(CommandSender sender, String[] args){
        Player p = (Player) sender;
        p.sendMessage("§c足迹模式已关闭！ 你的脚印将不会显示在服务器内！");
        taskMap.put(p, false);
    }

    @Cmd(coolDown = 2000, perm = "onyx.command.footstep", permMessage = "§c你需要 §bSVIP §c及以上的会员等级才能使用显示足迹！ 请移步到大厅进行购买会员等级！", only = CommandOnly.PLAYER)
    public void footstep(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(taskMap.containsKey(p) && taskMap.get(p)) {
            p.sendMessage("§c足迹模式已关闭！ 你的脚印将不会显示在服务器内！");
            taskMap.put(p, false);
        }else{
            p.sendMessage("§a足迹模式已开启！ 你的脚印将被显示在服务器内！");
            setCoolDown(p, 0);
            taskMap.put(p, true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(taskMap.containsKey(p) && taskMap.get(p) && spawnParticle(p)){
            Location loc = p.getLocation();
            if (!loc.clone().subtract(0, 1, 0).getBlock().isEmpty()) {
                double x = Math.cos(Math.toRadians(p.getLocation().getYaw())) * 0.25d;
                double y = Math.sin(Math.toRadians(p.getLocation().getYaw())) * 0.25d;
                if (dataMap.containsKey(p) && dataMap.get(p))
                    loc.add(x, 0.025D, y);
                else
                    loc.subtract(x, -0.025D, y);
                Main.getNMS().sendFootStep(loc);
                if(dataMap.containsKey(p)) {
                    dataMap.put(p, !dataMap.get(p));
                }else{
                    dataMap.put(p, true);
                }
            }
            setCoolDown(p, 150);
        }
    }

    private void setCoolDown(Player p, long mills) {
        coolDownMap.put(p, System.currentTimeMillis() + mills);
    }

    private boolean spawnParticle(Player p) {
        return coolDownMap.containsKey(p) && coolDownMap.get(p) <= System.currentTimeMillis();
    }

}
