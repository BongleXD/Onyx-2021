package net.blastmc.onyx.api.bukkit;

import io.netty.channel.Channel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface NMS {

    Hologram newInstance(Location loc, List<String> list);

    Hologram newInstance(Location loc, String... lines);

    Hologram newInstance(Location loc);

    void sendActionBar(Player p, String message);

    Channel getChannel(Player p);

    void sendFootStep(Location loc);

    void changeNameTag(Player sendTo, Player p, String prefix, String suffix, TeamAction action, String priority);

    void crashClient(Player p);

    void openBookMenu(Player p, ItemStack book);

    void changeName(Player p, String name);

    void restoreName(Player p);

    void reloadPlayer(Player p);

    void hidePlayer(Player p, Player sendTo);

    void showPlayer(Player p, Player sendTo);

}
