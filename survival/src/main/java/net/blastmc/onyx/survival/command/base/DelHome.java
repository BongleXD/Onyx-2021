package net.blastmc.onyx.survival.command.base;

import net.blastmc.onyx.bukkit.command.CommandManager;

public class DelHome extends CommandManager {

    public DelHome() {
        super("delhome", "删除家", "/delhome <家名字>", "survival.command.sethome");
    }
}
