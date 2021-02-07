package net.blastmc.onyx.survival.command.base;

import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.survival.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HomeList extends CommandManager {

    public HomeList() {
        super("homelist", "返回上一个死亡点", "/homelist 家园列表", "survival.command.homelist");
    }

    @Cmd(coolDown = 3000, perm = "survival.command.homelist", only = CommandOnly.PLAYER)
    public void homeList(CommandSender sender, String[] args) throws SQLException {
        Player p = (Player) sender;
        Connection conn = Main.getSql().getConnection();
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT homename FROM player_home WHERE uuid='" + p.getUniqueId().toString() + "';");
        boolean hasHome = false;
        for(int i = 0; rs.next(); i++){
            if(i == 0){
                hasHome = true;
                p.sendMessage("§6§m-----------------------------");
            }
            String homeName = rs.getString(1);
            TextComponent accept = new TextComponent("§a[点击传送]");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + homeName));
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击这里传送至此家园").create()));
            p.spigot().sendMessage(new TextComponent("§7" + i + ". " + homeName + " "), accept);
        }
        if (hasHome) {
            p.sendMessage("§6§m-----------------------------");
        } else {
            p.sendMessage("§c你当前还仍未设置任何家，输入 §b/sethome [家名称] §c来设置第一个家吧！");
        }
        rs.close();
        statement.close();
        conn.close();
    }

}
