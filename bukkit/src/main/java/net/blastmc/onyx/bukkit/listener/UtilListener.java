package net.blastmc.onyx.bukkit.listener;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.util.interact.inventory.ItemClickEvent;
import net.blastmc.onyx.bukkit.util.interact.inventory.SimpleInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.stream.Collectors;

public class UtilListener implements Listener {

    public UtilListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if(p != null && SimpleInventory.invList.stream().anyMatch(inv -> inv.getInv() == e.getClickedInventory())){
            SimpleInventory inv = SimpleInventory.invList
                    .stream()
                    .filter(simpleInv -> simpleInv.getInv() == e.getClickedInventory())
                    .collect(Collectors.toList()).get(0);
            for (ItemClickEvent clickEvent : inv.getListenerList()
                    .stream()
                    .filter(invEvent -> invEvent instanceof ItemClickEvent)
                    .map(invEvent -> (ItemClickEvent) invEvent)
                    .collect(Collectors.toList())) {
                clickEvent.onClick(p, inv.getInv(), e.getCurrentItem());
            }
        }
    }

}
