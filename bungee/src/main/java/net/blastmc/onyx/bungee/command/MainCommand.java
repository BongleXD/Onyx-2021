package net.blastmc.onyx.bungee.command;

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
                BroadcastConfig.cfg.reload();
                DataConfig.cfg.reload();
                LobbyConfig.cfg.reload();
                SkinConfig.cfg.reload();
                PunishConfig.cfg.reload();
                sender.sendMessage("§a配置文件已重置！");
            }
        }
    }
}
