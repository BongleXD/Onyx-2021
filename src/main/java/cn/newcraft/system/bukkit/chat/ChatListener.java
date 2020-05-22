package cn.newcraft.system.bukkit.chat;

import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.config.ChatConfig;
import cn.newcraft.system.bukkit.proxy.ServerType;
import cn.newcraft.system.bukkit.util.JSONUtil;
import cn.newcraft.system.bukkit.util.Method;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    public ChatListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if(ChatConfig.cfg.getBoolean("enabled")){
            Player p = e.getPlayer();
            PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
            e.setCancelled(true);
            TextComponent text = new TextComponent(PlaceholderAPI.setPlaceholders(e.getPlayer(), ChatConfig.cfg.getString("format")));
            if(ChatConfig.cfg.getBoolean("hover-enabled")) {
                HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PlaceholderAPI.setPlaceholders(p, ChatConfig.cfg.getString("hover-format"))).create());
                text.setHoverEvent(hover);
            }
            if(ChatConfig.cfg.getBoolean("click-enabled")) {
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ChatConfig.cfg.getString("click-command").replace("{player}", p.getName())));
            }
            String message = e.getMessage();
            boolean b = false;
            if(p.hasPermission("ncs.chat.gg")){
                if(message.equalsIgnoreCase("GG")){
                    message = "§6GG";
                    b = true;
                }else if(message.equalsIgnoreCase("Good Game")){
                    message = "§6Good Game";
                    b = true;
                }
            }
            if(!b){
                if(prof.isNicked() && ((Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME) || p.hasPermission("ncs.nick.staff")) && !prof.getNickPrefix().equals("self")){
                    if(prof.getNickPrefix().equals("§6[MVP§c++§6] ")){
                        message = Method.transColor(e.getMessage());
                    } else if(!prof.getNickPrefix().contains("§7")){
                        message = "§f" + message;
                    } else {
                        message = "§7" + message;
                    }
                }else {
                    if(p.hasPermission("ncs.chat.transcolor"))
                        message = Method.transColor(e.getMessage());
                    else if(p.hasPermission("ncs.chat.white"))
                        message = "§f" + message;
                    else
                        message = "§7" + message;
                }
            }
            for(Player online : Bukkit.getOnlinePlayers()){
                online.spigot().sendMessage(text, ComponentSerializer.parse(JSONUtil.toJSON(message))[0]);
            }
        }
    }

}
