package net.blastmc.onyx.survival;

import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.survival.command.base.*;
import net.blastmc.onyx.survival.home.HomeDatabase;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

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
        try {
            homeDB = new HomeDatabase();
            homeDB.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CommandManager.regCommand(new Home(), this);
        CommandManager.regCommand(new HomeList(), this);
        CommandManager.regCommand(new SetHome(), this);
        CommandManager.regCommand(new DelHome(), this);
        CommandManager.regCommand(new Back(), this);
    }

    @Override
    public void onDisable() {
        homeDB.close();
    }
}
