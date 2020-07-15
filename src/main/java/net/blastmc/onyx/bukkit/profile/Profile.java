package net.blastmc.onyx.bukkit.profile;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.proxy.ServerType;
import net.blastmc.onyx.bukkit.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Profile extends CommandManager implements Listener {

    public Profile() {
        super("profile", "查看档案", "/profile [玩家]", "玩家档案");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        this.setPermission("");
    }

    @Cmd(coolDown = 5000, only = CommandOnly.PLAYER)
    public void profile(CommandSender sender, String[] args){
        if(Main.getType() != ServerType.GAME && Main.getType() != ServerType.ENDLESS_GAME){
            new ProfileGui((Player) sender, sender.getName());
        }else{
            sender.sendMessage("§c请在大厅中查看玩家档案！");
        }
    }

    @Cmd(coolDown = 5000, arg = "<value>", only = CommandOnly.PLAYER)
    public void profileOther(CommandSender sender, String[] args){
        if(Main.getType() != ServerType.GAME && Main.getType() != ServerType.ENDLESS_GAME) {
            new ProfileGui((Player) sender, args[0]);
        }else{
            sender.sendMessage("§c请在大厅中查看玩家档案！");
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        try {
            Player p = (Player) e.getWhoClicked();
            if(e.getInventory().getTitle().contains("的游戏档案")){
                if(e.getCurrentItem().getType() == Material.BARRIER){
                    p.closeInventory();
                }
                e.setCancelled(true);
            }
        }catch (NullPointerException ignored){}
    }


}
