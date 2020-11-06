package net.blastmc.onyx.bukkit.command.admin;

import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.api.Onyx;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.config.BungeeConfig;
import net.blastmc.onyx.bukkit.util.Method;
import net.blastmc.onyx.shared.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Ban extends CommandManager {

    public Ban() {
        super("ban", "封禁", "/ban <玩家> <时间> <原因>", "封禁");
    }

    @Cmd(arg = "<value...>", perm = "onyx.command.ban", permMessage = "§c你需要 §2客服 及以上的会员等级才能使用此指令！")
    public void ban(CommandSender sender, String[] args){
        Player p = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList()).get(0);
        if(p != null){
            String pid = Onyx.getApi().getPID(args[0]);
            if(pid == null){
                sender.sendMessage("§c玩家不存在！");
                return;
            }
            String duration;
            try {
                if (args[1].endsWith("d")) {
                    duration = String.valueOf(Integer.parseInt(args[1].substring(0, args[1].length() - 1)) * 1000 * 60 * 60 * 24);
                } else if (args[1].endsWith("m")) {
                    duration = String.valueOf(Integer.parseInt(args[1].substring(0, args[1].length() - 1)) * 1000 * 60 * 60 * 24 * 30);
                } else if (args[1].endsWith("y")) {
                    duration = String.valueOf(Integer.parseInt(args[1].substring(0, args[1].length() - 1)) * 1000 * 60 * 60 * 24 * 30 * 12);
                } else {
                    duration = String.valueOf(Integer.parseInt(args[1].substring(0, args[1].length() - 1)));
                }
            }catch (NumberFormatException ex){
                sender.sendMessage("§c时间格式不对！例子: 7d (7 天), 1m (1个月), 1y (1 年), 1000 (1000 秒)");
                return;
            }
            args[0] = args[args.length - 1];
            args = Arrays.copyOf(args, args.length - 1);
            args[1] = args[args.length - 1];
            args = Arrays.copyOf(args, args.length - 1);
            String reason = Method.transColor(Joiner.on(" ").join(args));
            ByteArrayDataOutput b = ByteStreams.newDataOutput();
            b.writeUTF("BAN_PLAYER");
            b.writeUTF(pid);
            b.writeUTF(sender instanceof Player ? PlayerData.getDataFromUUID(((Player) sender).getUniqueId()).getName() : "CONSOLE");
            b.writeUTF(reason);
            b.writeUTF(duration);
            b.writeUTF(PlayerData.getDataFromUUID(p.getUniqueId()).getName());
            p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
        }
    }

}
