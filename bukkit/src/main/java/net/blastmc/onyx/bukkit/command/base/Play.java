package net.blastmc.onyx.bukkit.command.base;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Play extends CommandManager {

    public Play() {
        super("play", "传送服务器", "/play <目标服务器>");
        this.setPermission(null);
    }

    @Cmd(arg = "<value>", coolDown = 1000, only = CommandOnly.PLAYER)
    public void play(CommandSender sender, String[] args){
        Player p = (Player) sender;
        send(p, args[0]);
    }

    private void send(Player p, String name) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(name);
            p.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
