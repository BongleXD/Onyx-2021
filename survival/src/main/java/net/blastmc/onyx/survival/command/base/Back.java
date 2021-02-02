package net.blastmc.onyx.survival.command.base;

import net.blastmc.onyx.bukkit.command.CommandManager;

public class Back extends CommandManager {

    public Back() {
        super("back", "返回上一个死亡点", "/back", "survival.command.back");
    }
}
