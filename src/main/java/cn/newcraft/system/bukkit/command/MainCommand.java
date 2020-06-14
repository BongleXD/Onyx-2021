package cn.newcraft.system.bukkit.command;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.shared.PluginInfo;
import cn.newcraft.system.bukkit.util.plugin.PluginManager;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class MainCommand extends CommandManager{

    public MainCommand() {
        super("newcraftsystem", "NewCraftSystem 主命令", "/newcraftsystem help", "ncs", "core");
        this.setPermission(null);
    }

    @Cmd
    public void mainCommand(CommandSender sender, String[] args){
        sender.sendMessage("§9" + PluginInfo.getPlugin() + " §cPlugin §aCoded By: §e" + PluginInfo.getAuthor() + " §cCurrent Plugin Version: §e" + PluginInfo.getVersion());
    }

    @Cmd(arg = "help", perm = "ncs.command.main.help")
    public void mainHelp(CommandSender sender, String[] args) {
        sender.sendMessage("§8>§e========§9" + PluginInfo.getPlugin() + "§e========§8<");
        sender.sendMessage("§7/newcraftsystem help §8- §a获取帮助");
        sender.sendMessage("§7/newcraftsystem plugin §8- §a服务器插件管理器");
        sender.sendMessage("§7/newcraftsystem version §8- §a查看插件版本");
        sender.sendMessage("§7/newcraftsystem reload §8- §a重载配置文件");
        sender.sendMessage("§7/newcraftsystem reloadplugin §8- §a热重载" + PluginInfo.getPlugin() + " §c[不安全操作]");
        sender.sendMessage("§8>§e===========§cV" + PluginInfo.getVersion() + "§e===========§8<");
    }

    @Cmd(arg = "plugin", perm = "ncs.command.main.plugin")
    public void mainPlugin(CommandSender sender, String[] args) {
        sender.sendMessage(PluginInfo.INFO + " §e/newcraftsystem plugin list §7- §b列出已启用插件列表");
        sender.sendMessage(PluginInfo.INFO + " §e/newcraftsystem plugin enable <插件名> §7- §b启用指定插件");
        sender.sendMessage(PluginInfo.INFO + " §e/newcraftsystem plugin disable <插件名> §7- §b关闭指定插件");
        sender.sendMessage(PluginInfo.INFO + " §e/newcraftsystem plugin load <插件名> §7- §b加载指定插件");
        sender.sendMessage(PluginInfo.INFO + " §e/newcraftsystem plugin unload <插件名> §7- §b卸载指定插件");
        sender.sendMessage(PluginInfo.INFO + " §e/newcraftsystem plugin reload <插件名> §7- §b重载指定插件");
    }

    @Cmd(arg = "plugin list", perm = "ncs.command.main.plugin.list")
    public void mainPluginList(CommandSender sender, String[] args) {
        //boolean includeVersions = Method.hasFlag(args, 'v');
        List<Plugin> list = Lists.newArrayList();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            list.add(plugin);
        }
        sender.sendMessage("§9§m----------------------------");
        sender.sendMessage("§e总数： §a" + list.size());
        list.forEach(plugin -> {
            sender.sendMessage((plugin.isEnabled() ? "§a" : "§c") + plugin.getDescription().getName() + "§e, 作者: §a" + Joiner.on("§e, §a").join(plugin.getDescription().getAuthors()) + "§e, 版本: §a" + plugin.getDescription().getVersion());
        });
        sender.sendMessage("§9§m----------------------------");
    }

    @Cmd(arg = "plugin enable <value>", perm = "ncs.command.main.plugin.enable")
    public void mainPluginEnable(CommandSender sender, String[] args) {
        Plugin targetPlugin = PluginManager.getPluginByName(args, 2);
        if (targetPlugin == null) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + args[2] + " §c不存在！");
            return;
        }
        if (targetPlugin.isEnabled()) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + targetPlugin + " §c已经启用到了服务器！");
            return;
        }
        sender.sendMessage(PluginInfo.INFO + " §e插件 " + targetPlugin + " §e启用中...");
        PluginManager.enable(targetPlugin);
        sender.sendMessage(PluginInfo.INFO + " §a插件 " + targetPlugin + " §a已成功启用！");
    }

    @Cmd(arg = "plugin disable <value>", perm = "ncs.command.main.plugin.disable")
    public void mainPluginDisable(CommandSender sender, String[] args) {
        Plugin targetPlugin = PluginManager.getPluginByName(args, 2);
        if (targetPlugin == null) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + args[2] + " §c不存在或已经卸载！");
            return;
        }
        if (!targetPlugin.isEnabled()) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + targetPlugin + " §c已经被关闭了！");
            return;
        }
        sender.sendMessage(PluginInfo.INFO + " §e插件 " + targetPlugin + " §e关闭中...");
        Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin(args[2]));
        sender.sendMessage(PluginInfo.INFO + " §a插件 " + targetPlugin + " §a已成功关闭！");
    }

    @Cmd(arg = "plugin load <value>", perm = "ncs.command.main.plugin.load")
    public void mainPluginLoad(CommandSender sender, String[] args) {
        Plugin targetPlugin = PluginManager.getPluginByName(args, 2);
        if (targetPlugin != null) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + targetPlugin + " §c已经加载到了服务器！");
            return;
        }
        String name = Method.consolidateStrings(args, 2);
        try {
            sender.sendMessage(PluginInfo.INFO + " §e插件 " + name + " §e开始载入服务器中...");
            sender.sendMessage(PluginManager.load(name));
        } catch (Exception e) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + name + " §c因为未知原因导致插件载入失败！");
        }
    }

    @Cmd(arg = "plugin unload <value>", perm = "ncs.command.main.plugin.unload")
    public void mainPluginUnload(CommandSender sender, String[] args) {
        Plugin targetPlugin = PluginManager.getPluginByName(args, 2);
        if (targetPlugin == null) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + args[2] + " §c不存在或已经卸载！");
            return;
        }
        try {
            sender.sendMessage(PluginInfo.INFO + " §e插件 " + targetPlugin + " §e开始从服务器中卸载...");
            PluginManager.unload(targetPlugin);
            sender.sendMessage(PluginInfo.INFO + " §a插件 " + targetPlugin + " §a已成功从服务器中卸载！");
        } catch (Exception e) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + targetPlugin + " §c因为未知原因导致插件卸载失败！");
        }
    }

    @Cmd(arg = "plugin reload <value>", perm = "ncs.command.main.plugin.reload")
    public void mainPluginReload(CommandSender sender, String[] args) {
        Plugin target = PluginManager.getPluginByName(args, 2);
        if (target == null) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + args[2] + " §c不存在或已经卸载！");
            return;
        }
        try {
            sender.sendMessage(PluginInfo.INFO + " §e插件 " + target + " §e开始从服务器中重载...");
            PluginManager.reload(target);
            sender.sendMessage(PluginInfo.INFO + "§a插件 " + target + " §a已成功从服务器中重载！");
        } catch (Exception e) {
            sender.sendMessage(PluginInfo.ERROR + " §c插件 " + target + " §c因为未知原因导致插件重载失败！");
        }
    }

    @Cmd(arg = "version", perm = "ncs.command.main.version")
    public void mainVersion(CommandSender sender, String[] args) {
        sender.sendMessage("§2=====================================");
        sender.sendMessage("§9" + PluginInfo.getPlugin() + " §6Version: §c" + PluginInfo.getVersion());
        sender.sendMessage("§9Author: §6" + PluginInfo.getAuthor());
        sender.sendMessage("§9Description: §6This plugin is made by " + PluginInfo.getAuthor());
        sender.sendMessage("§9Commands: §6Use '/newcraftsystem help' Type for help!");
        sender.sendMessage("§2=====================================");
    }

    @Cmd(arg = "reload", perm = "ncs.command.main.reload")
    public void mainReload(CommandSender sender, String[] args) {
        Main.getInstance().reloadConfigs();
        sender.sendMessage("§a成功重载配置文件！");
    }

    @Cmd(arg = "reloadplugin", perm = "ncs.command.main.reloadplugin")
    public void mainReloadPlugin(CommandSender sender, String[] args) {
        sender.sendMessage(PluginInfo.INFO + " §c正在执行热重载" + PluginInfo.getPlugin() + "中...");
        PluginManager.reload(Main.getInstance());
        sender.sendMessage(PluginInfo.INFO + " §a已完成热重载！");
    }

}
