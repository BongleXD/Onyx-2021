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

public class Warn extends CommandManager {

    public Warn() {
        super("warn", "警告", "/warn <玩家> [原因]", "警告");
    }

    @Cmd(arg = "<value...>", perm = "onyx.command.warn", permMessage = "§c你需要 §9志愿者 及以上的会员等级才能使用此指令！")
    public void warn(CommandSender sender, String[] args){
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
            ByteArrayDataOutput b = ByteStreams.newDataOutput();
            b.writeUTF("WARN_PLAYER");
            b.writeUTF(pid);
            b.writeUTF(sender instanceof Player ? Onyx.getPlayerData(((Player) sender).getUniqueId()).getName() : "CONSOLE");
            b.writeUTF(reason);
            b.writeUTF(Onyx.getPlayerData(p.getUniqueId()).getName());
            p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
        }
    }

}
