package net.blastmc.onyx.bukkit.command.admin;

import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.api.utils.Log;
import net.blastmc.onyx.api.utils.Method;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Ban extends CommandManager {

    public Ban() {
        super("ban", "封禁", "/ban <玩家> [时间] [原因]", "onyx.command.ban");
    }

    @Cmd(arg = "<value...>", perm = "onyx.command.ban", permMessage = "§c你需要 §2客服 及以上的会员等级才能使用此指令！")
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
                    duration = Long.parseLong(args[1].substring(0, args[1].length() - 1)) * 1000 * 60 * 60 * 24;
                } else if (args[1].endsWith("m")) {
                    duration = Long.parseLong(args[1].substring(0, args[1].length() - 1)) * 1000 * 60 * 60 * 24 * 30;
                } else if (args[1].endsWith("y")) {
                    duration = Long.parseLong(args[1].substring(0, args[1].length() - 1)) * 1000 * 60 * 60 * 24 * 30 * 12;
                } else {
                    duration = Long.parseLong(args[1]) * 1000;
                }
            } catch (Exception ex) {
                duration = -1;
            }
            Log.getLogger().sendRawMessage("正在执行封禁时长: " + duration);
            String[] reasonArgs;
            if(duration != -1 && !args[1].equals("-1")){
                reasonArgs = new String[args.length - 2];
                System.arraycopy(args, 2, reasonArgs, 0, args.length - 2);
            }else{
                reasonArgs = new String[args.length - 1];
                System.arraycopy(args, 1, reasonArgs, 0, args.length - 1);
            }
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
