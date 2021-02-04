package net.blastmc.onyx.bukkit.command.base;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Tpa extends CommandManager implements Listener {

    private HashMap<UUID, List<UUID>> requests = new HashMap<>();
    private HashMap<UUID, BukkitTask> teleportMap = new HashMap<>();

    public Tpa() {
        super("tpa", "传送", "/tpa <玩家> 或 /tpa <accept/deny>", "onyx.command.tpa");
        this.setPermission(null);
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Cmd(arg = "accept <player>", only = CommandOnly.PLAYER, permMessage = "§c你不能这么做！", perm = "onyx.command.tpa")
    public void tpaAccept(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if(requests.containsKey(p.getUniqueId()) && !requests.get(p.getUniqueId()).isEmpty()) {
            Player requester = Bukkit.getPlayer(args[1]);
            p.sendMessage("§a你接受了请求！");
            requester.sendMessage("§a对方接受了请求！请勿移动即将在 §e3 §a秒后执行传送！");
            requests.get(p.getUniqueId()).remove(requester.getUniqueId());
            teleportMap.put(requester.getUniqueId(), new BukkitRunnable(){
                @Override
                public void run() {
                    requester.teleport(p.getLocation());
                    requester.sendMessage("§a正在将你传送至 " + p.getDisplayName());
                    teleportMap.remove(requester.getUniqueId());
                }
            }.runTaskLater(Main.getInstance(), 60));
        }else{
            p.sendMessage("§c你目前未接受到任何请求！");
        }
    }

    @Cmd(arg = "deny <player>", only = CommandOnly.PLAYER, permMessage = "§c你不能这么做！", perm = "onyx.command.tpa")
    public void tpaDeny(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if(requests.containsKey(p.getUniqueId()) && !requests.get(p.getUniqueId()).isEmpty()) {
            p.sendMessage("§a已拒绝 " + Bukkit.getPlayer(requests.get(p.getUniqueId()).get(0)).getDisplayName() + " §a的请求！");
            requests.get(p.getUniqueId()).remove(Bukkit.getPlayer(args[1]).getUniqueId());
        }else{
            p.sendMessage("§c你目前未接受到任何请求！");
        }
    }

    @Cmd(arg = "<value>", only = CommandOnly.PLAYER, permMessage = "§c你不能这么做！", perm = "onyx.command.tpa")
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
                if(!requests.get(target.getUniqueId()).contains(p.getUniqueId())){
                    requests.get(target.getUniqueId()).add(p.getUniqueId());
                }else{
                    p.sendMessage("§c你已经向对方发送了一个传送请求！");
                    return;
                }
            }else{
                requests.put(target.getUniqueId(), new ArrayList<>(Collections.singleton(p.getUniqueId())));
            }
            p.sendMessage("§a已将请求发送至 " + target.getDisplayName());
            TextComponent accept = new TextComponent("§a[接受]");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa accept " + p.getName()));
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击这里同意请求").create()));
            TextComponent deny = new TextComponent("§c[拒绝]");
            deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa deny " + p.getName()));
            deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击这里拒绝请求").create()));
            target.sendMessage("§9§m--------------------------");
            target.spigot().sendMessage(new TextComponent("§e玩家 " + p.getDisplayName() + " §e向你发送了传送请求！ "));
            target.spigot().sendMessage(new TextComponent("§e你有 §c60 §e秒来处理请求！ "), accept, new TextComponent("   "), deny);
            target.sendMessage("§9§m--------------------------");
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                if(requests.containsKey(target.getUniqueId()) && requests.get(target.getUniqueId()).contains(p.getUniqueId())){
                    target.sendMessage("§9§m--------------------------");
                    target.sendMessage("§e来自玩家 " + p.getDisplayName() + " §e的传送请求已过期！ ");
                    target.sendMessage("§9§m--------------------------");
                    p.sendMessage("§9§m--------------------------");
                    p.sendMessage("§e向玩家 " + target.getDisplayName() + " §e发送的传送请求已过期！ ");
                    p.sendMessage("§9§m--------------------------");
                    requests.get(target.getUniqueId()).remove(p.getUniqueId());
                }
            }, 20L * 60);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(e.getTo().getX() != e.getFrom().getX() || e.getTo().getY() != e.getFrom().getY() || e.getTo().getZ() != e.getFrom().getZ()){
            if(teleportMap.containsKey(p.getUniqueId())){
                teleportMap.get(p.getUniqueId()).cancel();
                teleportMap.remove(p.getUniqueId());
                p.sendMessage("§c你在即将传送时移动了，传送取消！");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        for(UUID uuid : requests.keySet()){
            if(!requests.get(uuid).isEmpty()){
                requests.get(uuid).remove(e.getPlayer().getUniqueId());
            }
        }
    }

}
