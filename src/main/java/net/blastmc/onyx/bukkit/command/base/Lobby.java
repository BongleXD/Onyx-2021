package net.blastmc.onyx.bukkit.command.base;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.api.PlayerProfile;
import net.blastmc.onyx.shared.PlayerData;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.config.BungeeConfig;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Lobby extends CommandManager {

    public Lobby() {
        super("lobby", "返回大厅", "/lobby", "l", "hub", "leave", "quit");
        setPermission("");
    }

    @Cmd(only = CommandOnly.PLAYER)
    public void onLobby(CommandSender sender, String[] args){
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            synchronized (this) {
                Player p = (Player) sender;
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                    if (p.isOnline()) {
                        p.sendMessage("§c发生错误！ §7无法连接到主大厅！");
                    }
                }, 5 * 20);
                PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
                if (prof != null) {
                    prof.saveData(false);
                }
                ByteArrayDataOutput b = ByteStreams.newDataOutput();
                b.writeUTF("BACK_LOBBY");
                b.writeUTF(BungeeConfig.cfg.getYml().getString("settings.lobby-servers"));
                b.writeUTF(PlayerData.getDataFromUUID(p.getUniqueId()).getName());
                p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
            }
        });
    }
}
