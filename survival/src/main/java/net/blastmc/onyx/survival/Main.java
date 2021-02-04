package net.blastmc.onyx.survival;

import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.survival.command.base.Back;
import net.blastmc.onyx.survival.command.base.DelHome;
import net.blastmc.onyx.survival.command.base.SetHome;
import net.blastmc.onyx.survival.home.HomeDatabase;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private HomeDatabase homeDB;

    public static Main getInstance() {
        return instance;
    }

    public static HomeDatabase getSql() {
        return instance.homeDB;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        instance = this;
        homeDB = new HomeDatabase();
        homeDB.init();
        CommandManager.regCommand(new SetHome(), this);
        CommandManager.regCommand(new DelHome(), this);
        CommandManager.regCommand(new Back(), this);
    }

    @Override
    public void onDisable() {

    }
}
