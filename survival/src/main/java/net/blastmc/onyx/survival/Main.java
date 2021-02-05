package net.blastmc.onyx.survival;

import com.google.common.collect.Lists;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.survival.command.base.*;
import net.blastmc.onyx.survival.home.HomeDatabase;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private HomeDatabase homeDB;
    private static Map<UUID, BukkitTask> tpQueue = new HashMap<>();

    public static Main getInstance() {
        return instance;
    }

    public static HomeDatabase getSql() {
        return instance.homeDB;
    }

    public static Map<UUID, BukkitTask> getTpQueue() {
        return tpQueue;
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
