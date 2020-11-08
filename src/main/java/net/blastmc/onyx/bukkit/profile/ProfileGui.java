package net.blastmc.onyx.bukkit.profile;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.gui.PlayerGui;
import net.blastmc.onyx.shared.util.SQLHelper;
import org.bukkit.entity.Player;

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
