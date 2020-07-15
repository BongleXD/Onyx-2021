package net.blastmc.onyx.bukkit.command.base;

import net.blastmc.onyx.bukkit.util.FireworkUtil;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Firework extends CommandManager {

    public Firework() {
        super("firework", "发射烟花", "/firework", "fw", "烟花");
        this.setPermissionMessage("§c你需要 §aVIP§6+ §c及以上的会员等级才能使用烟花！请移步至主大厅进行购买会员等级！");
    }

    @Cmd(coolDown = 5000, perm = "onyx.command.firework", permMessage = "§c你需要 §aVIP§6+ §c及以上的会员等级才能使用烟花！请移步至主大厅进行购买会员等级！", only = CommandOnly.PLAYER)
    public void firework(CommandSender sender, String[] args){
        Player p = (Player) sender;
        FireworkUtil fw = new FireworkUtil();
        fw.launch(p.getLocation());
        p.sendMessage("§a咻！");
    }

}
