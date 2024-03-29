package net.blastmc.onyx.bukkit.chat;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.server.ServerType;
import net.blastmc.onyx.bukkit.utils.JsonMessageUtil;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.config.ChatConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class ChatListener implements Listener {

    public ChatListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent e){
        if(e.getReason().equalsIgnoreCase("请不要刷屏！") || e.getReason().equalsIgnoreCase("disconnect.spam") || e.getReason().contains("spam")){
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        if(e.isCancelled()){
            return;
        }
        if(ChatConfig.ENABLED) {
            Player p = e.getPlayer();
            PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
            if(prof == null){
                return;
            }
            e.setCancelled(true);
            TextComponent text = new TextComponent(PlaceholderAPI.setPlaceholders(e.getPlayer(), ChatConfig.FORMAT));
            if (ChatConfig.HOVER_ENABLED) {
                HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PlaceholderAPI.setPlaceholders(p, ChatConfig.HOVER_FORMAT)).create());
                text.setHoverEvent(hover);
            }
            if (ChatConfig.CLICK_ENABLED) {
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ChatConfig.CLICK_COMMAND.replace("{player}", p.getName())));
            }
            String message = e.getMessage();
            boolean b = false;
            ChatColor color = ChatColor.GRAY;
            if (p.hasPermission("onyx.chat.gg") && prof.isNicked() && prof.getNickPrefix().equalsIgnoreCase("self") || p.hasPermission("onyx.chat.gg") && !prof.isNicked() || prof.isNicked() && (prof.getNickPrefix().equals("§6[SVIP§c+§6] "))) {
                if (message.equalsIgnoreCase("GG")) {
                    message = "GG";
                    color = ChatColor.GOLD;
                    b = true;
                } else if (message.equalsIgnoreCase("Good Game")) {
                    message = "Good Game";
                    color = ChatColor.GOLD;
                    b = true;
                }
            }
            if (!b) {
                if (prof.isNicked() && ((Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME) || p.hasPermission("onyx.nick.staff")) && !prof.getNickPrefix().equals("self")) {
                    if (!prof.getNickPrefix().contains("§7")) color = ChatColor.WHITE;
                } else {
                    if (p.hasPermission("onyx.chat.white")) color = ChatColor.WHITE;
                }
            }
            if (p.hasPermission("onyx.chat.transcolor") && prof.isNicked() && prof.getNickPrefix().equalsIgnoreCase("self") || p.hasPermission("onyx.chat.transcolor") && !prof.isNicked() || prof.isNicked() && (prof.getNickPrefix().equals("§6[SVIP§c+§6] ") || prof.getNickPrefix().equals("§6[SVIP§c+§6] "))) {
                message =  ChatColor.translateAlternateColorCodes('&', message);
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.spigot().sendMessage(text, ComponentSerializer.parse(JsonMessageUtil.toJson(color.toString() + message))[0]);
            }
            Bukkit.getConsoleSender().sendMessage(PlaceholderAPI.setPlaceholders(e.getPlayer(), ChatConfig.FORMAT) + message);
        }
    }

}
