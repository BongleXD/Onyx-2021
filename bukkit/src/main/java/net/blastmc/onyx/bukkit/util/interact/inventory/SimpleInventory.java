package net.blastmc.onyx.bukkit.util.interact.inventory;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class SimpleInventory {

    public static List<SimpleInventory> invList;
    private Inventory inv;
    private int size;
    private String title;
    private List<InventoryEvent> listenerList;

    static {
        invList = Lists.newArrayList();
    }

    public SimpleInventory(){
        listenerList = Lists.newArrayList();
    }

    public SimpleInventory title(String title){
        this.title = title;
        return this;
    }

    public SimpleInventory size(int size){
        this.size = size;
        return this;
    }

    public SimpleInventory addListener(InventoryEvent e){
        listenerList.add(e);
        return this;
    }

    public void create(){
        if(size > 6){
            size = 6;
        }
        this.inv = Bukkit.createInventory(null, size * 9, title);
        invList.add(this);
    }

    public Inventory getInv() {
        return inv;
    }

    public List<InventoryEvent> getListenerList() {
        return listenerList;
    }

}
