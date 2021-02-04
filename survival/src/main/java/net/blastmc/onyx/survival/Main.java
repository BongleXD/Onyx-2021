package net.blastmc.onyx.survival;

import com.google.common.collect.Lists;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.survival.command.base.Back;
import net.blastmc.onyx.survival.command.base.DelHome;
import net.blastmc.onyx.survival.command.base.Home;
import net.blastmc.onyx.survival.command.base.SetHome;
import net.blastmc.onyx.survival.home.HomeDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private HomeDatabase homeDB;
    private static List<UUID> tpQueue = Lists.newArrayList();

    public static Main getInstance() {
        return instance;
    }

    public static HomeDatabase getSql() {
        return instance.homeDB;
    }

    public static List<UUID> getTpQueue() {
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
        CommandManager.regCommand(new SetHome(), this);
        CommandManager.regCommand(new DelHome(), this);
        CommandManager.regCommand(new Back(), this);
    }

    @Override
    public void onDisable() {
        homeDB.close();
    }
}
