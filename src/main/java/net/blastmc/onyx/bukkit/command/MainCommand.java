package net.blastmc.onyx.bukkit.command;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.util.BukkitMethod;
import net.blastmc.onyx.bukkit.util.plugin.PluginManager;
import net.blastmc.onyx.shared.PluginInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class MainCommand extends CommandManager{

    public MainCommand() {
        super("onyx", "Onyx 主命令", "/onyx help", "core");
        this.setPermission(null);
    }

    @Cmd
    public void mainCommand(CommandSender sender, String[] args){
        sender.sendMessage("§9" + PluginInfo.getPlugin() + " §e由 §a" + PluginInfo.getAuthor() + " §e编写， 当前插件版本 §b" + PluginInfo.getVersion());
    }

    @Cmd(arg = "help", perm = "onyx.command.main.help")
    public void mainHelp(CommandSender sender, String[] args) {
        sender.sendMessage("§c/onyx help §7- §b获取帮助");
        sender.sendMessage("§c/onyx plugin §7- §b服务器插件管理器");
        sender.sendMessage("§c/onyx version §7- §b查看插件版本");
        sender.sendMessage("§c/onyx reload §7- §b重载配置文件");
    }

    @Cmd(arg = "plugin", perm = "onyx.command.main.plugin")
    public void mainPlugin(CommandSender sender, String[] args) {
        sender.sendMessage("§c/onyx plugin list §7- §b列出已启用插件列表");
        sender.sendMessage("§c/onyx plugin enable <插件名> §7- §b启用指定插件");
        sender.sendMessage("§c/onyx plugin disable <插件名> §7- §b关闭指定插件");
        sender.sendMessage("§c/onyx plugin load <插件名> §7- §b加载指定插件");
        sender.sendMessage("§c/onyx plugin unload <插件名> §7- §b卸载指定插件");
        sender.sendMessage("§c/onyx plugin reload <插件名> §7- §b重载指定插件");
    }

    @Cmd(arg = "plugin list", perm = "onyx.command.main.plugin.list")
    public void mainPluginList(CommandSender sender, String[] args) {
        List<Plugin> list = Lists.newArrayList();
        list.addAll(Arrays.asList(Bukkit.getPluginManager().getPlugins()));
        sender.sendMessage("§9§m----------------------------");
        sender.sendMessage("§e总数： §a" + list.size());
        list.forEach(plugin -> {
            sender.sendMessage((plugin.isEnabled() ? "§a" : "§c") + plugin.getDescription().getName() + "§e, 作者: §a" + Joiner.on("§e, §a").join(plugin.getDescription().getAuthors()) + "§e, 版本: §a" + plugin.getDescription().getVersion());
        });
        sender.sendMessage("§9§m----------------------------");
    }

    @Cmd(arg = "plugin enable <value>", perm = "onyx.command.main.plugin.enable")
    public void mainPluginEnable(CommandSender sender, String[] args) {
        Plugin targetPlugin = PluginManager.getPluginByName(args, 2);
        if (targetPlugin == null) {
            sender.sendMessage("§c插件 " + args[2] + " §c不存在！");
            return;
        }
        if (targetPlugin.isEnabled()) {
            sender.sendMessage("§c插件 " + targetPlugin + " §c已经启用到了服务器！");
            return;
        }
        sender.sendMessage("§e插件 " + targetPlugin + " §e启用中...");
        PluginManager.enable(targetPlugin);
        sender.sendMessage("§a插件 " + targetPlugin + " §a已成功启用！");
    }

    @Cmd(arg = "plugin disable <value>", perm = "onyx.command.main.plugin.disable")
    public void mainPluginDisable(CommandSender sender, String[] args) {
        Plugin targetPlugin = PluginManager.getPluginByName(args, 2);
        if (targetPlugin == null) {
            sender.sendMessage("§c插件 " + args[2] + " §c不存在或已经卸载！");
            return;
        }
        if (!targetPlugin.isEnabled()) {
            sender.sendMessage("§c插件 " + targetPlugin + " §c已经被关闭了！");
            return;
        }
        sender.sendMessage("§e插件 " + targetPlugin + " §e关闭中...");
        Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin(args[2]));
        sender.sendMessage("§a插件 " + targetPlugin + " §a已成功关闭！");
    }

    @Cmd(arg = "plugin load <value>", perm = "onyx.command.main.plugin.load")
    public void mainPluginLoad(CommandSender sender, String[] args) {
        Plugin targetPlugin = PluginManager.getPluginByName(args, 2);
        if (targetPlugin != null) {
            sender.sendMessage("§c插件 " + targetPlugin + " §c已经加载到了服务器！");
            return;
        }
        String name = BukkitMethod.consolidateStrings(args, 2);
        try {
            sender.sendMessage("§e插件 " + name + " §e开始载入服务器中...");
            sender.sendMessage(PluginManager.load(name));
        } catch (Exception e) {
            sender.sendMessage("§c插件 " + name + " §c因为未知原因导致插件载入失败！");
        }
    }

    @Cmd(arg = "plugin unload <value>", perm = "onyx.command.main.plugin.unload")
    public void mainPluginUnload(CommandSender sender, String[] args) {
        Plugin targetPlugin = PluginManager.getPluginByName(args, 2);
        if (targetPlugin == null) {
            sender.sendMessage("§c插件 " + args[2] + " §c不存在或已经卸载！");
            return;
        }
        try {
            sender.sendMessage("§e插件 " + targetPlugin + " §e开始从服务器中卸载...");
            PluginManager.unload(targetPlugin);
            sender.sendMessage("§a插件 " + targetPlugin + " §a已成功从服务器中卸载！");
        } catch (Exception e) {
            sender.sendMessage("§c插件 " + targetPlugin + " §c因为未知原因导致插件卸载失败！");
        }
    }

    @Cmd(arg = "plugin reload <value>", perm = "onyx.command.main.plugin.reload")
    public void mainPluginReload(CommandSender sender, String[] args) {
        Plugin target = PluginManager.getPluginByName(args, 2);
        if (target == null) {
            sender.sendMessage("§c插件 " + args[2] + " §c不存在或已经卸载！");
            return;
        }
        try {
            sender.sendMessage("§e插件 " + target + " §e开始从服务器中重载...");
            PluginManager.reload(target);
            sender.sendMessage("§a插件 " + target + " §a已成功从服务器中重载！");
        } catch (Exception e) {
            sender.sendMessage("§c插件 " + target + " §c因为未知原因导致插件重载失败！");
        }
    }

    @Cmd(arg = "version", perm = "onyx.command.main.version")
    public void mainVersion(CommandSender sender, String[] args) {
        sender.sendMessage("§9§m----------------------------------------");
        sender.sendMessage("§9" + PluginInfo.getPlugin() + " §e版本: §b" + PluginInfo.getVersion());
        sender.sendMessage("§e作者: §b" + PluginInfo.getAuthor());
        sender.sendMessage("§9§m----------------------------------------");
    }

    @Cmd(arg = "reload", perm = "onyx.command.main.reload")
    public void mainReload(CommandSender sender, String[] args) {
        Main.getInstance().reloadConfigs();
        sender.sendMessage("§a成功重载配置文件！");
    }

}
