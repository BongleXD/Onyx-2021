package cn.newcraft.system.bukkit.nick;

import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.util.Method;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UnNick extends CommandManager {

    public UnNick() {
        super("unnick", "昵称修改", "/unnick", "昵称修改");
        this.setPermission("ncs.command.nick");
    }

    @Cmd(coolDown = 5000, perm = "ncs.command.nick", only = CommandOnly.PLAYER)
    public void nick(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getGameManager() != null || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        if(prof.isNicked()) {
            Main.getNMS().restoreName(p);
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
