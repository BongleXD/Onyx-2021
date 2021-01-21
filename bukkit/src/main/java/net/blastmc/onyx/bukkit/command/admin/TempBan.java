package net.blastmc.onyx.bukkit.command.admin;

import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.api.util.Method;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TempBan extends CommandManager {

    public TempBan() {
        super("tempban", "封禁", "/tempban <玩家> <时间> [原因]", "onyx.command.tempban");
    }

    @Cmd(arg = "<value...>", perm = "onyx.command.tempban", permMessage = "§c你需要 §2客服 及以上的会员等级才能使用此指令！")
    public void ban(CommandSender sender, String[] args){
        Player p = new ArrayList<Player>(Bukkit.getOnlinePlayers()).get(0);
        if(p != null){
            String pid = Onyx.getAPI().getPIDIgnoreNick(args[0]);
            if(pid == null){
                sender.sendMessage("§c玩家不存在！");
                return;
            }
            long duration;
            try {
                if (args[1].endsWith("d")) {
                    duration = Integer.parseInt(args[1].substring(0, args[1].length() - 1)) * 1000 * 60 * 60 * 24;
                } else if (args[1].endsWith("m")) {
                    duration = Integer.parseInt(args[1].substring(0, args[1].length() - 1)) * 1000 * 60 * 60 * 24 * 30;
                } else if (args[1].endsWith("y")) {
                    duration = Integer.parseInt(args[1].substring(0, args[1].length() - 1)) * 1000 * 60 * 60 * 24 * 30 * 12;
                } else {
                    duration = Integer.parseInt(args[1]) * 1000;
                }
            } catch (Exception ex) {
                sender.sendMessage("§c请输入天/月/年， 例如 3d/3m/3y， 或者秒数！");
                return;
            }
            if(duration <= 0){
                sender.sendMessage("§c请输入正整数！");
                return;
            }
            String[] reasonArgs = new String[args.length - 2];
            System.arraycopy(args, 2, reasonArgs, 0, args.length - 2);
            String reason = Method.transColor(Joiner.on(" ").join(reasonArgs));
            if(reason.isEmpty()){
                reason = "利用破坏游戏平衡性的方式获取不平等优势！";
            }
            ByteArrayDataOutput b = ByteStreams.newDataOutput();
            b.writeUTF("BAN_PLAYER");
            b.writeUTF(pid);
            b.writeUTF(sender instanceof Player ? Onyx.getPlayerData(((Player) sender).getUniqueId()).getName() : "CONSOLE");
            b.writeUTF(reason);
            b.writeUTF(String.valueOf(duration));
            b.writeUTF(Onyx.getPlayerData(p.getUniqueId()).getName());
            p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
        }
    }

}
