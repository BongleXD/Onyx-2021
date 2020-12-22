package net.blastmc.onyx.bungee.command;

import net.blastmc.onyx.bungee.Main;
import net.blastmc.onyx.bungee.config.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MainCommand extends Command {

    public MainCommand() {
        super("onyx-bungee", "onyx.command.main");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("reload")){
                Main.getInstance().reload();
                sender.sendMessage("§a配置文件已重置！");
            }
        }
    }
}
