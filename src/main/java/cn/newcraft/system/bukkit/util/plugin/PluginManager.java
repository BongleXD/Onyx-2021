package cn.newcraft.system.bukkit.util.plugin;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.shared.PluginInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.*;

public class PluginManager {

    private static List<String> ignoredplugins = null;

    public static void enable(Plugin plugin) {
        if ((plugin != null) && (!plugin.isEnabled())) {
            Bukkit.getPluginManager().enablePlugin(plugin);
        }
    }

    public static void disable(Plugin plugin) {
        if ((plugin != null) && (plugin.isEnabled())) {
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public static String getFormattedName(Plugin plugin) {
        return getFormattedName(plugin, false);
    }

    public static String getFormattedName(Plugin plugin, boolean includeVersions) {
        ChatColor color = plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
        String pluginName = color + plugin.getName();
        if (includeVersions) {
            pluginName = pluginName + " (" + plugin.getDescription().getVersion() + ")";
        }
        return pluginName;
    }

    public static Plugin getPluginByName(String name) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (name.equalsIgnoreCase(plugin.getName())) {
                return plugin;
            }
        }
        return null;
    }

    public static List<String> getPluginNames(boolean fullName) {
        List<String> plugins = new ArrayList();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            plugins.add(fullName ? plugin.getDescription().getFullName() : plugin.getName());
        }
        return plugins;
    }

    public static String getPluginVersion(String name) {
        Plugin plugin = getPluginByName(name);
        if ((plugin != null) && (plugin.getDescription() != null)) {
            return plugin.getDescription().getVersion();
        }
        return null;
    }

    public static boolean isIgnored(Plugin plugin) {
        return isIgnored(plugin.getName());
    }

    public static boolean isIgnored(String plugin) {
        for (String name : ignoredplugins) {
            if (name.equalsIgnoreCase(plugin)) {
                return true;
            }
        }
        return false;
    }

    private static String load(Plugin plugin) {
        return load(plugin.getName());
    }

    public static String load(String name) {
        Plugin target = null;

        File pluginDir = new File("plugins");
        if (!pluginDir.isDirectory()) {
            return PluginInfo.ERROR + " §c未找到插件目录。";
        }
        File pluginFile = new File(pluginDir, name + ".jar");
        if (!pluginFile.isFile()) {
            for (File f : pluginDir.listFiles()) {
                if (f.getName().endsWith(".jar")) {
                    try {
                        PluginDescriptionFile desc = Main.getInstance().getPluginLoader().getPluginDescription(f);
                        if (desc.getName().equalsIgnoreCase(name)) {
                            pluginFile = f;
                            break;
                        }
                    } catch (InvalidDescriptionException e) {
                        return PluginInfo.ERROR + " §c找不到文件，无法搜索说明。";
                    }
                }
            }
        }
        try {
            target = Bukkit.getPluginManager().loadPlugin(pluginFile);
        } catch (InvalidDescriptionException e) {
            e.printStackTrace();
            return PluginInfo.ERROR + " §c该插件的描述无效。";
        } catch (InvalidPluginException e) {
            return PluginInfo.ERROR + " §c在插件目录和未找到 " + name + " 插件，请确认插件是否存在！";
        }
        target.onLoad();
        Bukkit.getPluginManager().enablePlugin(target);
        return PluginInfo.INFO + " §a插件 " + name + " §a已成功载入到了服务器！";
    }

    public static void reload(Plugin plugin) {
        if (plugin != null) {
            unload(plugin);
            load(plugin);
        }
    }

    public static Plugin getPluginByName(String[] args, int start) {
        return getPluginByName(Method.consolidateStrings(args, start));
    }

    public static String unload(Plugin plugin) {
        String name = plugin.getName();

        org.bukkit.plugin.PluginManager pluginManager = Bukkit.getPluginManager();

        SimpleCommandMap commandMap = null;

        List<Plugin> plugins = null;

        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

        boolean reloadlisteners = true;
        if (pluginManager != null) {
            pluginManager.disablePlugin(plugin);
            try {
                Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                plugins = (List) pluginsField.get(pluginManager);

                Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                names = (Map) lookupNamesField.get(pluginManager);
                try {
                    Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
                    listenersField.setAccessible(true);
                    listeners = (Map) listenersField.get(pluginManager);
                } catch (Exception e) {
                    reloadlisteners = false;
                }
                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                commands = (Map) knownCommandsField.get(commandMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return PluginInfo.ERROR + " §c插件" + name + "卸载失败";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return PluginInfo.ERROR + " §c插件" + name + "卸载失败";
            }
        }
        Iterator<RegisteredListener> it;
        pluginManager.disablePlugin(plugin);
        if ((plugins != null)) {
            plugins.remove(plugin);
        }
        if ((names != null)) {
            names.remove(name);
        }
        if ((listeners != null) && (reloadlisteners)) {
            for (SortedSet<RegisteredListener> set : listeners.values()) {
                for (it = set.iterator(); it.hasNext(); ) {
                    RegisteredListener value = it.next();
                    if (value.getPlugin() == plugin) {
                        it.remove();
                    }
                }
            }
        }
        Iterator<Map.Entry<String, Command>> it2;
        if (commandMap != null) {
            for (it2 = commands.entrySet().iterator(); it2.hasNext(); ) {
                Map.Entry<String, Command> entry = it2.next();
                if ((entry.getValue() instanceof PluginCommand)) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == plugin) {
                        c.unregister(commandMap);
                        it2.remove();
                    }
                }
            }
        }
        ClassLoader cl = plugin.getClass().getClassLoader();
        if ((cl instanceof URLClassLoader)) {
            try {
                ((URLClassLoader) cl).close();
            } catch (IOException ignored) {
            }
        }

        return name + PluginInfo.INFO + " §a卸载成功！";
    }
}

