package net.blastmc.onyx.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TpTo extends Command {

    public TpTo() {
        super("tpto", "onyx.command.tpto", "teleportserver", "tpserver");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)){
            sender.sendMessage("§c该指令不能在控制台执行！");
            return;
        }
        if (!(args.length >= 1)){
            sender.sendMessage("§c用法： /tpto <server/player>");
            return;
        }
        boolean player = false;
        String server = null;
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target != null){
            player = true;
            server = target.getServer().getInfo().getName();
        }
        if (!player) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(p.getServer().getInfo().getName().contains("login")){
                p.sendMessage("§c你不能这么做！");
                return;
            }
            if (ProxyServer.getInstance().getServerInfo(args[0]) != null){
                p.sendMessage("§a正在将你传送至服务器 " + args[0]);
                p.connect(ProxyServer.getInstance().getServerInfo(args[0]));
            } else {
                p.sendMessage("§c传送失败，该服务器不存在！");
            }
        } else {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            p.sendMessage("§a正在将你传送至服务器 " + server);
            p.connect(ProxyServer.getInstance().getServerInfo(server));
        }
    }
}
