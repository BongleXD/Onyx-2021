package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.SystemAPI;
import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.config.BungeeConfig;
import cn.newcraft.system.bukkit.exception.RebootException;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.bukkit.util.interact.SoundUtil;
import cn.newcraft.system.bukkit.util.interact.TitleUtil;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Reboot extends CommandManager {

    public Reboot() {
        super("reboot", "重启", "/reboot <时间> [原因] 或 /reboot stop", "shutdown");
    }

    @Cmd(arg = "stop", aliases = "暂停", perm = "ncs.command.reboot", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void stop(CommandSender sender, String[] args){
        if(Method.getTask("reboot") != null){
            Method.removeTask("reboot");
            sender.sendMessage("§c关闭服务器操作被强行终止！");
            for(Player online : Bukkit.getOnlinePlayers()){
                TitleUtil.sendTitle(online, 3, 15, 3, "§c关闭操作被终止", "");
            }
        }else{
            sender.sendMessage("§c未检测到服务器正在进行重启！");
        }
    }

    @Cmd(arg = "<integer>", perm = "ncs.command.reboot", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void reboot(CommandSender sender, String[] args){
        Bukkit.dispatchCommand(sender, "reboot " + args[0] + " 重启服务器");
    }

    @Cmd(arg = "<integer> <value>", perm = "ncs.command.reboot", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void rebootReason(CommandSender sender, String[] args){
        if(sender instanceof Player) {
            Player p = (Player) sender;
            TextComponent text = new TextComponent("§e你确定吗！ 点击这里执行重启！");
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reboot confirm " + args[0] + " " + args[1]));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c点击这里确认重启！").create()));
            p.spigot().sendMessage(text);
        }else{
            Bukkit.dispatchCommand(sender, "reboot confirm " + args[0] + " " + args[1]);
        }
    }

    @Cmd(arg = "confirm <integer>", perm = "ncs.command.reboot", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void rebootConfirm(CommandSender sender, String[] args){
        Bukkit.dispatchCommand(sender, "reboot confirm " + args[0] + " 重启服务器");
    }

    @Cmd(arg = "confirm <integer> <value>", perm = "ncs.command.reboot", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void rebootConfirmReason(CommandSender sender, String[] args){
        if(Method.getTask("reboot") != null){
            sender.sendMessage("§c服务器重启执行中！");
            return;
        }
        sender.sendMessage("§e已执行服务器重启操作！");
        BukkitTask task = new BukkitRunnable(){
            int i = Integer.parseInt(args[1]);
            @Override
            public void run() {
                if(i <= 0){
                    Bukkit.broadcastMessage("§c服务器已重启！");
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        shutdown(online, args[2]);
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
                }else if(i % 10 == 0 || i <= 5) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.playSound(online.getLocation(), SoundUtil.NOTE_STICKS, 2.0F, 1.0F);
                        TitleUtil.sendTitle(online, 3, 15, 3, "§c服务器即将于 §e" + i + " §c秒后关闭", "§e原因: " + args[2]);
                    }
                }
                i--;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
        Method.createTask("reboot", task);
    }

    private void shutdown(Player p, String reason) {
        SystemAPI.getApi().kickToLobby(Main.getInstance(), p, BungeeConfig.cfg.getYml().getString("settings.lobby-servers"), BungeeConfig.cfg.getYml().getString("settings.lobby-server-name"), reason);
    }
}
