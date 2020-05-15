package cn.newcraft.system.bukkit.command.base;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.config.BungeeConfig;
import cn.newcraft.system.shared.PlayerData;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.BungeeCord;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Lobby extends CommandManager {

    public Lobby() {
        super("lobby", "返回大厅", "/lobby", "l", "hub", "leave", "quit");
        setPermission("");
    }

    @Cmd(only = CommandOnly.PLAYER)
    public void onLobby(CommandSender sender, String[] args){
        Player p = (Player)sender;
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            if(p.isOnline()){
                p.sendMessage("§c发生错误！ §7无法连接到主大厅！");
            }
        }, 5 * 20);
        ByteArrayDataOutput b = ByteStreams.newDataOutput();
        b.writeUTF("BACK_LOBBY");
        b.writeUTF(BungeeConfig.cfg.getYml().getString("BungeeCord.LobbyServer"));
        b.writeUTF(PlayerData.getDataFromUUID(p.getUniqueId()).getName());
        p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }
}
