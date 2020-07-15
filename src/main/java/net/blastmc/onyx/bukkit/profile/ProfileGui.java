package net.blastmc.onyx.bukkit.profile;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.gui.PlayerGui;
import net.blastmc.onyx.bukkit.util.Method;
import net.blastmc.onyx.bukkit.util.interact.ItemBuilder;
import net.blastmc.onyx.shared.util.SQLHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProfileGui extends PlayerGui {

    private static SQLHelper sql = Main.getSQL();
    private String owner;

    public ProfileGui(Player p, String owner){
        this.owner = owner;
        open(p);
    }

    @Override
    public void open(Player p) {
        p.sendMessage("§c敬请期待！");
    }

}
