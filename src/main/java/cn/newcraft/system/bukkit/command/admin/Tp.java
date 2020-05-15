package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.util.Method;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tp extends CommandManager {

    public Tp() {
        super("tp", "传送", "/tp <玩家> [玩家] 或者 /tp [玩家] <x> <y> <z>", "传送");
    }

    @Cmd(arg = "<player>", perm = "ncs.command.tp", permMessage = "§c你需要 §2MOD §c及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void tp(CommandSender sender, String[] args){
        Player p = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        p.teleport(target);
        p.sendMessage("§a已将你传送至玩家 " + target.getDisplayName());
    }

    @Cmd(arg = "<player> <player>", perm = "ncs.command.tp.force", permMessage = "§c你需要 ADMIN §c及以上的会员等级才能使用此指令！")
    public void tpForce(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        Player target = Bukkit.getPlayer(args[1]);
        p.teleport(target);
        sender.sendMessage("§a已将 " + p.getDisplayName() + " §a传送至玩家 " + target.getDisplayName());
    }

    @Cmd(arg = "<value> <value> <value>", perm = "ncs.command.tp.loc", permMessage = "§c你需要 ADMIN §c及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void tpLoc(CommandSender sender, String[] args){
        Player p = (Player) sender;
        String x = null;
        String y = null;
        String z = null;
        for(int i = 0; i < 3; i++){
            if(args[i].startsWith("~")){
                String s = args[i].replace("~", "");
                if(s.isEmpty() | checkDecimal(s)){
                    if(i == 0){
                        x = String.valueOf(s.isEmpty() ? 0 + p.getLocation().getX() : Double.parseDouble(s) + p.getLocation().getX());
                    }else if(i == 1){
                        y = String.valueOf(s.isEmpty() ? 0 + p.getLocation().getY() : Double.parseDouble(s) + p.getLocation().getY());
                    }else{
                        z = String.valueOf(s.isEmpty() ? 0 + p.getLocation().getZ() : Double.parseDouble(s) + p.getLocation().getZ());
                    }
                }
            }else{
                String s = args[i];
                if(checkDecimal(s)){
                    if(i == 0){
                        x = s;
                    }else if(i == 1){
                        y = s;
                    }else{
                        z = s;
                    }
                }
            }
        }
        if(x == null || y == null || z == null){
            p.sendMessage("§c请输入数字！");
        }else{
            p.teleport(new Location(p.getWorld(), Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z)));
            p.sendMessage("§a已将你传送至 " + Method.roundDouble(Double.parseDouble(x), 1) + ", " + Method.roundDouble(Double.parseDouble(y), 1) + ", " + Method.roundDouble(Double.parseDouble(z), 1));
        }
    }

    @Cmd(arg = "<player> <value> <value> <value>", perm = "ncs.command.tp.loc.force", permMessage = "§c你需要 ADMIN §c及以上的会员等级才能使用此指令！")
    public void tpLocForce(CommandSender sender, String[] args){
        Player p = Bukkit.getPlayer(args[0]);
        String x = null;
        String y = null;
        String z = null;
        for(int i = 1; i < 4; i++){
            if(args[i].startsWith("~")){
                String s = args[i].replace("~", "");
                if(s.isEmpty() | checkDecimal(s)){
                    if(i == 1){
                        x = String.valueOf(s.isEmpty() ? 0 + p.getLocation().getX() : Double.parseDouble(s) + p.getLocation().getX());
                    }else if(i == 2){
                        y = String.valueOf(s.isEmpty() ? 0 + p.getLocation().getY() : Double.parseDouble(s) + p.getLocation().getY());
                    }else{
                        z = String.valueOf(s.isEmpty() ? 0 + p.getLocation().getZ() : Double.parseDouble(s) + p.getLocation().getZ());
                    }
                }
            }else{
                String s = args[i];
                if(checkDecimal(s)){
                    if(i == 1){
                        x = s;
                    }else if(i == 2){
                        y = s;
                    }else{
                        z = s;
                    }
                }
            }
        }
        if(x == null || y == null || z == null){
            sender.sendMessage("§c请输入数字！");
        }else{
            p.teleport(new Location(p.getWorld(), Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z)));
            sender.sendMessage("§a已将玩家 " + p.getDisplayName() + " §a传送至 " + Method.roundDouble(Double.parseDouble(x), 1) + ", " + Method.roundDouble(Double.parseDouble(y), 1) + ", " + Method.roundDouble(Double.parseDouble(z), 1));
            if(p != sender) {
                p.sendMessage("§a已将你传送至 " + Method.roundDouble(Double.parseDouble(x), 1) + ", " + Method.roundDouble(Double.parseDouble(y), 1) + ", " + Method.roundDouble(Double.parseDouble(z), 1));
            }
        }
    }

}
