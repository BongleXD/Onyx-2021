package cn.newcraft.system.bukkit.support;

import cn.newcraft.system.bukkit.util.TeamAction;
import io.netty.channel.Channel;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface NMS {

    void sendActionBar(Player p, String message);

    Channel getChannel(Player p);

    String levelUP();

    String joinSound();

    String quitSound();

    String NOTE_STICKS();

    void sendFootStep(Location loc);

    void changeNameTag(Player sendTo, Player p, String prefix, String suffix, TeamAction action, String priority);

    void crashClient(Player p);

    void openBookMenu(Player p, ItemStack book);

    void changeName(Player p, String name);

    void restoreName(Player p);

    void reloadPlayer(Player p);

}
