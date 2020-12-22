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
import java.util.Arrays;
import java.util.stream.Collectors;

public class Kick extends CommandManager {

    public Kick() {
        super("kick", "移除", "/kick <玩家> [原因]", "移除");
    }

    @Cmd(arg = "<value...>", perm = "onyx.command.kick", permMessage = "§c你需要 §2客服 及以上的会员等级才能使用此指令！")
    public void kick(CommandSender sender, String[] args){
        Player p = new ArrayList<Player>(Bukkit.getOnlinePlayers()).get(0);
        if(p != null){
            String pid = Onyx.getAPI().getPIDIgnoreNick(args[0]);
            if(pid == null){
                sender.sendMessage("§c玩家不存在！");
                return;
            }
            String[] reasonArgs = new String[args.length - 1];
            System.arraycopy(args, 1, reasonArgs, 0, args.length - 1);
            String reason = Method.transColor(Joiner.on(" ").join(reasonArgs));
            if(reason.isEmpty()){
                reason = "你被从服务器移除， 请重新加入服务器！";
            }
            ByteArrayDataOutput b = ByteStreams.newDataOutput();
            b.writeUTF("KICK_PLAYER");
            b.writeUTF(pid);
            b.writeUTF(sender instanceof Player ? Onyx.getPlayerData(((Player) sender).getUniqueId()).getName() : "CONSOLE");
            b.writeUTF(reason);
            b.writeUTF(Onyx.getPlayerData(p.getUniqueId()).getName());
            p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
        }
    }

}
