package cn.newcraft.system.bukkit.level;

import cn.newcraft.system.bukkit.proxy.ServerType;
import cn.newcraft.system.bukkit.util.interact.SoundUtil;
import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.api.event.PlayerLevelUPEvent;
import cn.newcraft.system.bukkit.api.event.PlayerXpGainEvent;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.util.interact.TitleUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.TreeMap;

public class LevelListener implements Listener {

    public LevelListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        PlayerData data = PlayerData.getDataFromUUID(e.getPlayer().getUniqueId());
        if(data != null) {
            new ClaimData(data.getPID());
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e){
        try {
            ClaimData data = ClaimData.getDataFromUUID(e.getPlayer().getUniqueId());
            if (data != null) {
                data.saveData(true);
            }
        }catch (Exception ignored){}
    }

    @EventHandler
    public void onLevelUp(PlayerLevelUPEvent e){
        Player p = e.getPlayer();
        PlayerProfile profile = PlayerProfile.getDataFromUUID(p.getUniqueId());
        int newLevel = e.getNewLevel();
        TextComponent text = new TextComponent("         §e点击这里领取你的等级奖励！");
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rewards"));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击这里，打开领取界面！").create()));
        TreeMap<Integer, Double> xpMap = BoostReward.XP.getBoost();
        TreeMap<Integer, Double> coinMap = BoostReward.COIN.getBoost();
        p.sendMessage("§9▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
        p.sendMessage("");
        p.sendMessage("                  §a§kK §r§6升级！§a§kK§r                ");
        p.sendMessage("");
        p.sendMessage("     §7你目前的 §bNew§fCraft §7等级 为 §aLv." + e.getNewLevel());
        p.sendMessage("");
        if(xpMap.containsKey(newLevel) && coinMap.containsKey(newLevel)){
            p.sendMessage("      §b" + xpMap.get(newLevel) + "x §7经验, §6" + coinMap.get(newLevel) + "x §7工艺币加成 §a已解锁！");
            p.sendMessage("");
            profile.setXpBoost(xpMap.get(newLevel));
            profile.setCoinBoost(coinMap.get(newLevel));
        }else if (xpMap.containsKey(newLevel)){
            p.sendMessage("           §b" + xpMap.get(newLevel) + "x §7经验加成 §a已解锁！");
            p.sendMessage("");
            profile.setXpBoost(xpMap.get(newLevel));
        }else if (coinMap.containsKey(newLevel)){
            p.sendMessage("           §6" + coinMap.get(newLevel) + "x §7工艺币加成 §a已解锁！");
            p.sendMessage("");
            profile.setCoinBoost(coinMap.get(newLevel));
        }
        if(Main.getType() != ServerType.GAME && Main.getType() != ServerType.ENDLESS_GAME) {
            p.spigot().sendMessage(text);
        }else{
            p.sendMessage("          §e请到大厅领取你的奖励！");
        }
        p.sendMessage("§9▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
        p.playSound(p.getLocation(), SoundUtil.LEVEL_UP, 1, 1);
        TitleUtil.sendTitle(p, 10, 10, 10, "", "§6升级");
    }

    @EventHandler
    public void onXpGain(PlayerXpGainEvent e){
        Player p = e.getPlayer();
        p.sendMessage("§b+" + e.getAmount() + " NewCraft EXP" + (e.getBoost() != 1.0 ? " (" + e.getBoost() + "x 经验加成)" : ""));
    }

}
