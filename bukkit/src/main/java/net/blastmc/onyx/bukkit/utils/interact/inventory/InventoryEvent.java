package net.blastmc.onyx.bukkit.utils.interact.inventory;

public class InventoryEvent {

    private boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
