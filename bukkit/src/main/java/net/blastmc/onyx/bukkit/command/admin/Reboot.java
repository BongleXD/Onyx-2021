package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.config.SettingConfig;
import net.blastmc.onyx.bukkit.utils.Method;
import net.blastmc.onyx.bukkit.utils.interact.SoundUtil;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.exception.RebootException;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Reboot extends CommandManager {

    public Reboot() {
        super("reboot", "重启", "/reboot <时间> [原因] 或 /reboot stop", "onyx.command.reboot", "shutdown");
    }

    @Cmd(arg = "stop", aliases = "暂停", perm = "onyx.command.reboot", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void stop(CommandSender sender, String[] args){
        if(Method.getTask("reboot") != null){
            Method.removeTask("reboot");
            sender.sendMessage("§c关闭服务器操作被强行终止！");
        }else{
            sender.sendMessage("§c未检测到服务器正在进行重启！");
        }
    }

    @Cmd(perm = "onyx.command.reboot", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void reboot(CommandSender sender, String[] args){
        Bukkit.dispatchCommand(sender, "reboot 30 计划性重启");
    }

    @Cmd(arg = "<integer>", perm = "onyx.command.reboot", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void rebootSec(CommandSender sender, String[] args){
        Bukkit.dispatchCommand(sender, "reboot " + args[0] + " 计划性重启");
    }

    @Cmd(arg = "<integer> <value>", perm = "onyx.command.reboot", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void rebootSecReason(CommandSender sender, String[] args){
        if(Method.getTask("reboot") != null){
            sender.sendMessage("§c服务器重启执行中！");
            return;
        }
        sender.sendMessage("§e已执行服务器重启操作！");
        BukkitTask task = new BukkitRunnable(){
            int i = Integer.parseInt(args[0]);
            @Override
            public void run() {
                if(i <= 0){
                    Bukkit.broadcastMessage("§c服务器已重启！");
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        shutdown(online, args[1]);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                throw new RebootException();
                            } catch (RebootException e) {
                                e.printStackTrace();
                            }
                            Bukkit.shutdown();
                            cancel();
                        }
                    }.runTaskLater(Main.getInstance(), 40L);
                    this.cancel();
                } else if(i == Integer.parseInt(args[0]) || i % 10 == 0 || i <= 5) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.playSound(online.getLocation(), SoundUtil.NOTE_PIANO.getSound(), 2.0F, 1.3F);
                        TextComponent text = new TextComponent("§a§n点击这里");
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hub"));
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击这里， 传送至大厅").create()));
                        online.sendMessage("");
                        online.sendMessage(new TextComponent("§c[重要信息] §e此服务器即将要进行重启: §b" + args[1]));
                        online.sendMessage(new TextComponent("§e你有 §a" + i + " 秒 §e来传送至大厅！ "), text, new TextComponent(" §e传送至大厅！"));
                        online.sendMessage("");
                    }
                }
                i--;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
        Method.createTask("reboot", task);
    }

    private void shutdown(Player p, String reason) {
        Onyx.getAPI().kickToLobby(p.getUniqueId(), SettingConfig.LOBBY, SettingConfig.LOBBY_NAME, reason);
    }

}
