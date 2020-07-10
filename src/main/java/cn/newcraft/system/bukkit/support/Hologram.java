package cn.newcraft.system.bukkit.support;

import org.bukkit.entity.Player;

public interface Hologram {

    double getOffset();

    Hologram offset(double value);

    Hologram showTo(Player p);

    Hologram removeTo(Player p);

    void show();

    void remove();

    Hologram line(String value);

    Hologram line(int line, String value);

    Hologram removeLine(int line);
}
