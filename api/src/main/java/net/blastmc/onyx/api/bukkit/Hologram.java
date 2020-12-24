package net.blastmc.onyx.api.bukkit;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface Hologram {

    Hologram animation(int line, Animation anim);

    List<String> getLines();

    Location getLocation();

    Hologram location(Location loc);

    double getOffset();

    Hologram offset(double value);

    Hologram showTo(Player p);

    Hologram showTo(Collection<? extends Player> list);

    Hologram removeTo(Player p);

    Hologram removeTo(Collection<? extends Player> list);

    void show();

    void remove();

    Hologram line(String value);

    Hologram line(int line, String value);

    Hologram removeLine(int line);

}
