package net.blastmc.onyx.survival.command.base;

import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.survival.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.*;

public class DelHome extends CommandManager {

    public DelHome() {
        super("delhome", "删除家", "/delhome <家名字>", "survival.command.delhome");
    }

    @Cmd(arg = "<value>", perm = "survival.command.delhome", coolDown = 1000, only = CommandOnly.PLAYER)
    public void onDelHome(CommandSender sender, String[] args) throws SQLException {
        Player p = (Player)sender;
        String name = args[0];
        Connection conn = Main.getSql().getConnection();
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM player_home WHERE homename = '" + name+"' AND uuid='"+p.getUniqueId().toString()+"';");
        if (result.next()) {
            if (result.getInt(1) != 0){
                PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM player_home WHERE homename='"+name+"' AND uuid='"+p.getUniqueId().toString()+"';");
                preparedStatement.executeUpdate();
                p.sendMessage("§a已删除家 §b"+name);
            } else {
                p.sendMessage("§c家 "+name+" 不存在！");
            }
        }
        result.close();
        statement.close();
        conn.close();
    }
}
