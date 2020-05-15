package cn.newcraft.system.bukkit.command.base;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class Tpa extends CommandManager implements Listener {

    private HashMap<UUID, List<UUID>> requests = new HashMap<>();

    public Tpa() {
        super("tpa", "传送", "/tpa <玩家> 或 /tpa <accept/deny>");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Cmd(arg = "accept <player>", only = CommandOnly.PLAYER, permMessage = "§c你不能这么做！", perm = "ncs.command.tpa")
    public void tpaAccept(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if(requests.containsKey(p.getUniqueId()) && !requests.get(p.getUniqueId()).isEmpty()) {
            Player requester = Bukkit.getPlayer(args[1]);
            requester.teleport(p.getLocation());
            p.sendMessage("§a你接受了请求！");
            requests.get(p.getUniqueId()).remove(requester.getUniqueId());
            requester.sendMessage("§a正在将你传送至 " + p.getDisplayName());
        }else{
            p.sendMessage("§c你目前未接受到任何请求！");
        }
    }

    @Cmd(arg = "deny <player>", only = CommandOnly.PLAYER, permMessage = "§c你不能这么做！", perm = "ncs.command.tpa")
    public void tpaDeny(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if(requests.containsKey(p.getUniqueId()) && !requests.get(p.getUniqueId()).isEmpty()) {
            p.sendMessage("§a已拒绝 " + Bukkit.getPlayer(requests.get(p.getUniqueId()).get(0)).getDisplayName() + " §a的请求！");
            requests.get(p.getUniqueId()).remove(Bukkit.getPlayer(args[1]).getUniqueId());
        }else{
            p.sendMessage("§c你目前未接受到任何请求！");
        }
    }

    @Cmd(arg = "<value>", only = CommandOnly.PLAYER, permMessage = "§c你不能这么做！", perm = "ncs.command.tpa")
    public void tpa(CommandSender sender, String[] args){
        Player p = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            if(args[0].equalsIgnoreCase("accept")){
                if(requests.containsKey(p.getUniqueId()) && !requests.get(p.getUniqueId()).isEmpty()) {
                    Player requester = Bukkit.getPlayer(requests.get(p.getUniqueId()).get(0));
                    requester.teleport(p.getLocation());
                    p.sendMessage("§a你接受了请求！");
                    requests.get(p.getUniqueId()).remove(requester.getUniqueId());
                    requester.sendMessage("§a正在将你传送至 " + p.getDisplayName());
                }else{
                    p.sendMessage("§c你目前未接受到任何请求！");
                }
            } else if (args[0].equalsIgnoreCase("deny")){
                if(requests.containsKey(p.getUniqueId()) && !requests.get(p.getUniqueId()).isEmpty()) {
                    p.sendMessage("§a已拒绝 " + Bukkit.getPlayer(requests.get(p.getUniqueId()).get(0)).getDisplayName() + " §a的请求！");
                    requests.get(p.getUniqueId()).remove(0);
                }else{
                    p.sendMessage("§c你目前未接受到任何请求！");
                }
            }else{
                p.sendMessage("§c玩家 " + args[0] + " 不存在！");
            }
        }else{
            if(requests.containsKey(target.getUniqueId())){
                requests.get(target.getUniqueId()).add(p.getUniqueId());
            }else{
                requests.put(target.getUniqueId(), new ArrayList<UUID>(Collections.singleton(p.getUniqueId())));
            }
            p.sendMessage("§a已将请求发送至 " + target.getDisplayName());
            TextComponent accept = new TextComponent("§a[接受]");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa accept " + p.getName()));
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击这里同意请求").create()));
            TextComponent deny = new TextComponent("§c[拒绝]");
            deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa deny " + p.getName()));
            deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击这里拒绝请求").create()));
            target.sendMessage("§9§m-----------------------------------------------------");
            target.spigot().sendMessage(new TextComponent("§e玩家 " + p.getDisplayName() + " §e向你发送了传送请求 "), accept, new TextComponent("   "), deny);
            target.sendMessage("§9§m-----------------------------------------------------");
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e){
        for(UUID uuid : requests.keySet()){
            if(!requests.get(uuid).isEmpty()){
                requests.get(uuid).remove(e.getPlayer().getUniqueId());
            }
        }
    }

}
