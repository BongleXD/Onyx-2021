package net.blastmc.onyx.survival.command.base;

import net.blastmc.onyx.api.utils.Method;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.survival.Main;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.*;

public class SetHome extends CommandManager {

    public SetHome() {
        super("sethome", "设置家", "/sethome [家名字]", "survival.command.sethome");
    }

    @Cmd(coolDown = 3000, perm = "survival.command.sethome", only = CommandOnly.PLAYER)
    public void empty(CommandSender sender, String[] args) throws SQLException {
        setHome(sender, args);
    }

    @Cmd(arg = "<value>", coolDown = 3000, perm = "survival.command.sethome", only = CommandOnly.PLAYER)
    public void nonEmpty(CommandSender sender, String[] args) throws SQLException {
        setHome(sender, args);
    }

    private void setHome(CommandSender sender, String[] args) throws SQLException {
        Player p = (Player) sender;
        Connection conn = Main.getSql().getConnection();
        PreparedStatement preparedStatement;
        Statement statement;
        ResultSet result;
        statement = conn.createStatement();
        result = statement.executeQuery("SELECT COUNT(*) FROM player_home WHERE uuid = '" + p.getUniqueId().toString()+"';");
        if (result.next()) {
            int count = result.getInt(1);
            statement.close();
            result.close();
            if (count > 5) {
                p.sendMessage("§c您当前可以设置的家已达最大值！");
            } else {
                String homeName = args.length == 0 ? "Home_"+count+1 : args[0];
                statement = conn.createStatement();
                result = statement.executeQuery("SELECT COUNT(*) FROM player_home WHERE homename = '"+homeName + "' AND uuid = '" + p.getUniqueId().toString() + "';");
                count = result.getInt(1);
                if (count != 0){
                    p.sendMessage("§c当前名字 "+homeName+" 与当前已存在的家名字冲突！");
                    statement.close();
                    result.close();
                    return;
                }
                statement.close();
                result.close();
                Location loc = p.getLocation();
                double x = loc.getX();
                double y = loc.getY();
                double z = loc.getZ();
                preparedStatement = conn.prepareStatement("INSERT INTO player_home (uuid, homename, world, x, y, z) VALUES('"+p.getUniqueId().toString()+"', '"+homeName+"', '"+p.getWorld().getName()+"', '"+x+"', '"+y+"', '"+z+"');");
                preparedStatement.executeUpdate();
                preparedStatement.close();
                p.sendMessage("§a已成功设置家 §b" + homeName + "§a 位于：§eX："+ Method.roundDouble(x, 1)+" Y："+Method.roundDouble(y, 1)+" Z："+Method.roundDouble(z, 1));
            }
        }
        conn.close();
    }
}
