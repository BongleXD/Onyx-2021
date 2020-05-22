package cn.newcraft.system.bukkit.level;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.config.RewardConfig;
import cn.newcraft.system.bukkit.proxy.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Rewards extends CommandManager implements Listener {

    private HashMap<UUID, Integer> pageMap = new HashMap<>();

    public Rewards(){
        super("rewards", "查看奖励", "/rewards");
        this.setPermission("");
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Cmd(only = CommandOnly.PLAYER)
    public void onCommand(CommandSender sender, String[] args){
        if(Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME ){
            sender.sendMessage("§c请在大厅领取等级奖励！");
            return;
        }
        UUID uuid = ((Player) sender).getUniqueId();
        pageMap.put(uuid, 1);
        new RewardGui(Bukkit.getPlayer(uuid), pageMap.get(uuid));
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
         try {
            if (e.getClickedInventory().getTitle().equals("NewCraft 等级奖励")) {
                Player p = (Player) e.getWhoClicked();
                UUID uuid = p.getUniqueId();
                String name = e.getCurrentItem().getItemMeta().getDisplayName();
                if (e.getCurrentItem().getItemMeta().getLore().contains("§e点击领取！")) {
                    ClaimData data = ClaimData.getDataFromUUID(uuid);
                    int level = Integer.parseInt(name.split(" ")[2].replace("Lv.", ""));
                    data.addClaim(level);
                    List<String> commands = RewardConfig.cfg.getYml().getStringList("rewards." + level + ".command");
                    p.playSound(p.getLocation(), Sound.valueOf(Main.getNMS().levelUP()), 1, 1);
                    for(String cmd : commands){
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", p.getName()));
                    }
                }
                if (name.contains("下一页")) {
                    pageMap.put(uuid, pageMap.get(uuid) + 1);
                } else if (name.contains("上一页")) {
                    pageMap.put(uuid, pageMap.get(uuid) - 1);
                }
                e.setCancelled(true);
                new RewardGui(p, pageMap.get(uuid));
            }
        }catch (NullPointerException ignored) {}
    }

}
