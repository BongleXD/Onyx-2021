package cn.newcraft.system.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

public class GList extends Command {

    public GList(){
        super("list-bungee", "ncs-bungee.command.list", "list-b", "glist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§6--------------------------");
        sender.sendMessage("§c当前在线玩家：  §8| §a§l" + BungeeCord.getInstance().getPlayers().size() + " §cPlayers");
        sender.sendMessage("§c服务器 §8| §c在线人数");
        int total = 0;

        Map<String, ServerInfo> servers = BungeeCord.getInstance().getServers();
        for (String name : servers.keySet()) {
            ServerInfo info = servers.get(name);

            int online = info.getPlayers().size();
            total += online;

            sender.sendMessage("§a" + info.getName() + " §8| §6" + online);
        }
        sender.sendMessage("§6--------------------------");
    }
}
