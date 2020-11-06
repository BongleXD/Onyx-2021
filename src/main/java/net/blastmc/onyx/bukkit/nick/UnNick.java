package net.blastmc.onyx.bukkit.nick;

import net.blastmc.onyx.bukkit.api.PlayerProfile;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.proxy.ServerType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UnNick extends CommandManager {

    public UnNick() {
        super("unnick", "昵称修改", "/unnick", "昵称修改");
        this.setPermission("onyx.command.nick");
    }

    @Cmd(coolDown = 5000, perm = "onyx.command.nick", only = CommandOnly.PLAYER)
    public void nick(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if((Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME) || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        if(prof.isNicked()) {
            if(p.hasPermission("onyx.nick.staff")) {
                Main.getNMS().restoreName(p);
            }
            prof.setNicked(false);
            prof.setNickPrefix("");
            prof.setNickSkin("");
            prof.setNickName("");
            new BukkitRunnable() {
                @Override
                public void run() {
                    prof.saveData(false);
                }
            }.runTaskAsynchronously(Main.getInstance());
            p.sendMessage("§a你的游戏昵称已还原！");
        }else{
            p.sendMessage("§c你目前未更改昵称！");
        }
    }
}
