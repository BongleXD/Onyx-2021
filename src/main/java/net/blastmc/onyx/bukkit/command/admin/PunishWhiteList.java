package net.blastmc.onyx.bukkit.command.admin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.shared.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class PunishWhiteList extends CommandManager {

    public PunishWhiteList() {
        super("punishwhitelist", "惩罚白名单", "/punishwhitelist <add/remove> <玩家>", "punishwl");
    }

    @Cmd(arg = "add <value>", perm = "onyx.command.punishwhitelist", permMessage = "§c你需要 §2客服 及以上的会员等级才能使用此指令！")
    public void addWL(CommandSender sender, String[] args){
        Player p = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList()).get(0);
        if(p != null){
            ByteArrayDataOutput b = ByteStreams.newDataOutput();
            b.writeUTF("ADD_PUNISH_WL");
            b.writeUTF(args[1]);
            b.writeUTF(PlayerData.getDataFromUUID(p.getUniqueId()).getName());
            p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
        }
    }

    @Cmd(arg = "remove <value>", perm = "onyx.command.punishwhitelist", permMessage = "§c你需要 §2客服 及以上的会员等级才能使用此指令！")
    public void removeWL(CommandSender sender, String[] args){
        Player p = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList()).get(0);
        if(p != null){
            ByteArrayDataOutput b = ByteStreams.newDataOutput();
            b.writeUTF("REMOVE_PUNISH_WL");
            b.writeUTF(args[1]);
            b.writeUTF(PlayerData.getDataFromUUID(p.getUniqueId()).getName());
            p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
        }
    }

}
