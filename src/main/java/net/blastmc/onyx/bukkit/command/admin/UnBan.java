package net.blastmc.onyx.bukkit.command.admin;

import com.google.common.base.Joiner;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.api.Onyx;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.util.BukkitMethod;
import net.blastmc.onyx.shared.PlayerData;
import net.blastmc.onyx.shared.util.Method;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UnBan extends CommandManager {

    public UnBan() {
        super("unban", "解除封禁", "/unban <玩家> [原因]");
    }

    @Cmd(arg = "<value...>", perm = "onyx.command.unban", permMessage = "§c你需要 §2客服 及以上的会员等级才能使用此指令！")
    public void unBan(CommandSender sender, String[] args){
        Player p = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList()).get(0);
        if(p != null){
            String pid = Onyx.getApi().getPID(args[0]);
            if(pid == null){
                sender.sendMessage("§c玩家不存在！");
                return;
            }
            args[0] = args[args.length - 1];
            args = Arrays.copyOf(args, args.length - 1);
            String reason = Method.transColor(Joiner.on(" ").join(args));
            ByteArrayDataOutput b = ByteStreams.newDataOutput();
            b.writeUTF("UNBAN_PLAYER");
            b.writeUTF(pid);
            b.writeUTF(sender instanceof Player ? PlayerData.getDataFromUUID(((Player) sender).getUniqueId()).getName() : "CONSOLE");
            b.writeUTF(reason);
            b.writeUTF(PlayerData.getDataFromUUID(p.getUniqueId()).getName());
            p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
        }
    }

}
