package net.blastmc.onyx.bukkit.level;

import net.blastmc.onyx.bukkit.api.PlayerProfile;
import net.blastmc.onyx.bukkit.config.RewardConfig;
import net.blastmc.onyx.bukkit.gui.PlayerGui;
import net.blastmc.onyx.bukkit.util.Method;
import net.blastmc.onyx.bukkit.util.interact.ItemBuilder;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RewardGui extends PlayerGui {

    private Player p;
    private int page;

    public RewardGui(Player p, int page){
        this.p = p;
        this.page = page;
        open(p);
    }

    @Override
    public void open(Player p) {
        PlayerProfile profile = PlayerProfile.getDataFromUUID(p.getUniqueId());
        Inventory inv = Bukkit.createInventory(null, 54, "BlastMC 等级奖励");
        int size = RewardConfig.cfg.getYml().getConfigurationSection("rewards").getKeys(false).size();
        int row = 2;
        int column = 2;
        int index = page * 14 - 14;
        int endIndex = Math.min(index + 14, size);
        for(; index < endIndex; index++){
            Method.setInvItem(inv, getItem(Integer.parseInt((String) RewardConfig.cfg.getYml().getConfigurationSection("rewards").getKeys(false).toArray()[index])), row, column);
            column++;
            if(column == 9){
                column = 2;
                row++;
                if(row == 4){
                    row = 2;
                }
            }
        }
        if(index < size){
            inv.setItem(53, new ItemBuilder(Material.ARROW)
                    .setName("§a下一页")
                    .addLoreLine("§e切换到页面 " + (page + 1))
                    .toItemStack());
        }
        if(page >= 2){
            inv.setItem(45, new ItemBuilder(Material.ARROW)
                    .setName("§a上一页")
                    .addLoreLine("§e切换到页面 " + (page - 1))
                    .toItemStack());
        }
        Method.setInvItem(inv, new ItemBuilder(Material.DIAMOND_BLOCK)
                .setName("§a你目前的 §b经验 §a加成: " + profile.getXpBoost() + "x")
                .addLoreLine("§8通过升级来提升 §b经验 §8加成")
                .addLoreLine("")
                .addLoreLine(getNextUnlockLevel(profile.getLevel(), BoostReward.XP))
                .toItemStack(), 5, 3);
        Method.setInvItem(inv, new ItemBuilder(Material.GOLD_BLOCK)
                .setName("§a你目前的 §f铁粒 §a加成: " + profile.getCoinBoost() + "x")
                .addLoreLine("§8通过升级来提升 §f铁粒 §8加成")
                .addLoreLine("")
                .addLoreLine(getNextUnlockLevel(profile.getLevel(), BoostReward.COIN))
                .toItemStack(), 5, 7);
        p.closeInventory();
        p.openInventory(inv);
    }

    private ItemStack getItem(int level){
        ClaimData data = ClaimData.getDataFromUUID(p.getUniqueId());
        PlayerProfile profile = PlayerProfile.getDataFromUUID(p.getUniqueId());
        List<String> lore = Lists.newArrayList();
        RewardConfig.cfg.getYml().getStringList("rewards." + level + ".lore").forEach(s -> {
            lore.add(Method.transColor(s));
        });
        if(profile.getLevel() < level){
            return new ItemBuilder(Material.STORAGE_MINECART)
                    .setName("§cBlastMC 等级奖励 Lv." + level)
                    .addLoreLines(lore)
                    .addLoreLine("")
                    .addLoreLine("§c你还未抵达当前等级！")
                    .toItemStack();
        } else if(data.getClaimList().contains(level)){
            return new ItemBuilder(Material.MINECART)
                    .setName("§aBlastMC 等级奖励 Lv." + level)
                    .addLoreLines(lore)
                    .addLoreLine("")
                    .addLoreLine("§a已领取！")
                    .toItemStack();
        } else{
            return new ItemBuilder(Material.STORAGE_MINECART)
                    .setName("§eBlastMC 等级奖励 Lv." + level)
                    .addLoreLines(lore)
                    .addLoreLine("")
                    .addLoreLine("§e点击领取！")
                    .toItemStack();
        }
    }

    private String getNextUnlockLevel(int level, BoostReward reward){
        for (int i : reward.getBoost().keySet()) {
            if(level < i){
                return "§c你还需要 Lv." + (i - level) + " 解锁新加成！";
            }
        }
        return "§a已解锁";
    }

}
