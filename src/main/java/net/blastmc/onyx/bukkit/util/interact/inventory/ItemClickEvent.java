package net.blastmc.onyx.bukkit.util.interact.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ItemClickEvent extends InventoryEvent {

    public abstract void onClick(Player p, Inventory inv, ItemStack item);

}
