package net.blastmc.onyx.bukkit.api;

import net.blastmc.onyx.bukkit.api.event.PlayerLevelUPEvent;
import net.blastmc.onyx.bukkit.api.event.PlayerXpGainEvent;
import net.blastmc.onyx.bukkit.rank.RankData;
import net.blastmc.onyx.bukkit.util.Method;
import net.blastmc.onyx.bukkit.util.interact.ActionBarUtil;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.proxy.ServerType;
import net.blastmc.onyx.shared.PlayerData;
import net.blastmc.onyx.shared.util.SQLHelper;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerProfile {

    private String pid;
    private int level;
    private int xp;
    private double xpBoost;
    private double coinBoost;
    private boolean vanish;
    private boolean nicked;
    private String nickName;
    private String nickPrefix;
    private String nickSkin;
    private String secondPasswd;
    private String prefix;
    private String suffix;
    private static HashMap<String, PlayerProfile> dataMap = new HashMap<>();
    private static SQLHelper sql = Main.getSQL();
    private static List<PlayerProfile> vanishs = Lists.newArrayList();

    public PlayerProfile(String pid, int level, int xp, double xpBoost, double coinBoost, boolean vanish, boolean nicked, String nickName, String nickPrefix, String nickSkin, String secondPasswd, String prefix, String suffix) {
        this.pid = pid;
        this.level = level;
        this.xp = xp;
        this.xpBoost = xpBoost;
        this.coinBoost = coinBoost;
        this.vanish = vanish;
        this.nicked = nicked;
        this.nickName = nickName;
        this.nickPrefix = nickPrefix;
        this.nickSkin = nickSkin;
        this.secondPasswd = secondPasswd;
        this.prefix = prefix;
        this.suffix = suffix;
        dataMap.put(pid, this);
    }

    public PlayerProfile(String pid) {
        this.pid = pid;
        putData();
        dataMap.put(pid, this);
    }

    private void putData() {
        List list = sql.getData("player_profile", "pid", pid, "net_level", "net_xp", "xp_boost", "coin_boost", "vanish", "nicked", "nick_name", "nick_prefix", "nick_skin", "second_passwd", "prefix", "suffix");
        this.level = (int) list.get(0);
        this.xp = (int) list.get(1);
        this.xpBoost = (double) list.get(2);
        this.coinBoost = (double) list.get(3);
        this.vanish = (int) list.get(4) != 0;
        this.nicked = (int) list.get(5) != 0;
        this.nickName = (String) list.get(6);
        this.nickPrefix = (String) list.get(7);
        this.nickSkin = (String) list.get(8);
        this.secondPasswd = (String) list.get(9);
        this.prefix = (String) list.get(10);
        this.suffix = (String) list.get(11);
    }

    public static void init() {
        sql.create("player_profile",
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "pid"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "net_level"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "net_xp"),
                new SQLHelper.Value(SQLHelper.ValueType.DECIMAL, "xp_boost"),
                new SQLHelper.Value(SQLHelper.ValueType.DECIMAL, "coin_boost"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "vanish"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "nicked"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "nick_name"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "nick_prefix"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "nick_skin"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "second_passwd"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "prefix"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "suffix"));
    }

    public String getPID() {
        return this.pid;
    }

    public UUID getUUID() {
        return PlayerData.getData(pid).getUUID();
    }

    public void addLevel(int value) {
        Bukkit.getPluginManager().callEvent(new PlayerLevelUPEvent(Bukkit.getPlayer(getUUID()), level, level + value));
        this.level = this.level + value;
    }

    public double getXpBoost() {
        return xpBoost;
    }

    public void setXpBoost(double xpBoost) {
        this.xpBoost = xpBoost;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
        checkLevelUp();
    }

    public void addXpWithNoEvent(int value) {
        double d = (double) value * xpBoost;
        value = (int) d;
        this.xp = value + xp;
        checkLevelUp();
    }

    public void addXp(int value) {
        double d = (double) value * xpBoost;
        value = (int) d;
        Bukkit.getPluginManager().callEvent(new PlayerXpGainEvent(Bukkit.getPlayer(getUUID()), value, xpBoost));
        this.xp = value + xp;
        checkLevelUp();
    }

    public void checkLevelUp() {
        if (this.xp >= getXpToLevelUp()) {
            this.xp = xp - getXpToLevelUp();
            addLevel(1);
            checkLevelUp();
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getXpToLevelUp() {
        return 10000 + (level * 2500);
    }

    public String getProgressBar() {
        return Method.getProgressBar(xp, getXpToLevelUp(), 20, "■", "§b", "§7");
    }

    public double getCoinBoost() {
        return coinBoost;
    }

    public void setCoinBoost(double coinBoost) {
        this.coinBoost = coinBoost;
    }

    public boolean isVanish() {
        return vanish;
    }

    public void setVanish(boolean vanish) {
        this.vanish = vanish;
        checkStatus();
    }

    public boolean isNicked() {
        return nicked;
    }

    public void setNicked(boolean nicked) {
        this.nicked = nicked;
        checkStatus();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickPrefix() {
        return nickPrefix;
    }

    public void setNickPrefix(String nickPrefix) {
        this.nickPrefix = nickPrefix;
    }

    public String getNickSkin() {
        return nickSkin;
    }

    public void setNickSkin(String nickSkin) {
        this.nickSkin = nickSkin;
    }

    public String getSecondPasswd() {
        return secondPasswd;
    }

    public void setSecondPasswd(String secondPasswd) {
        this.secondPasswd = secondPasswd;
    }

    public void checkStatus() {
        if ((isVanish() && isNicked() && (Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME)) || (isVanish() && isNicked() && Bukkit.getPlayer(getUUID()).hasPermission("onyx.nick.staff"))) {
            ActionBarUtil.cancel(Bukkit.getPlayer(getUUID()));
            ActionBarUtil.sendBar(Bukkit.getPlayer(getUUID()), "§f你目前§c已设置昵称, 隐身", -1);
        } else if (isVanish()) {
            ActionBarUtil.cancel(Bukkit.getPlayer(getUUID()));
            ActionBarUtil.sendBar(Bukkit.getPlayer(getUUID()), "§f你目前§c已隐身", -1);
        } else if ((isNicked() && (Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME)) || (isNicked() && Bukkit.getPlayer(getUUID()).hasPermission("onyx.nick.staff"))) {
            ActionBarUtil.cancel(Bukkit.getPlayer(getUUID()));
            ActionBarUtil.sendBar(Bukkit.getPlayer(getUUID()), "§f你目前§c已设置昵称", -1);
        } else {
            ActionBarUtil.cancel(Bukkit.getPlayer(getUUID()));
        }
    }

    public static List<PlayerProfile> getVanishs() {
        return vanishs;
    }

    public static void addVanishPlayer(PlayerProfile prof) {
        if (!vanishs.contains(prof)) {
            vanishs.add(prof);
        }
    }

    public static void removeVanishPlayer(PlayerProfile prof) {
        vanishs.remove(prof);
    }

    public static PlayerProfile getData(String pid) {
        return dataMap.getOrDefault(pid, null);
    }

    public static PlayerProfile getDataFromUUID(UUID uuid) {
        PlayerData data = PlayerData.getDataFromUUID(uuid);
        if (data == null) return null;
        return dataMap.getOrDefault(data.getPID(), null);
    }

    public void saveData(boolean destroy) {
        sql.putData("player_profile", "pid", this.pid,
                new SQLHelper.SqlValue("net_level", this.level),
                new SQLHelper.SqlValue("net_xp", this.xp),
                new SQLHelper.SqlValue("xp_boost", this.xpBoost),
                new SQLHelper.SqlValue("coin_boost", this.coinBoost),
                new SQLHelper.SqlValue("vanish", this.vanish ? 1 : 0),
                new SQLHelper.SqlValue("nicked", this.nicked ? 1 : 0),
                new SQLHelper.SqlValue("nick_name", this.nickName),
                new SQLHelper.SqlValue("nick_prefix", this.nickPrefix),
                new SQLHelper.SqlValue("nick_skin", this.nickSkin),
                new SQLHelper.SqlValue("second_passwd", this.secondPasswd),
                new SQLHelper.SqlValue("prefix", this.prefix),
                new SQLHelper.SqlValue("suffix", this.suffix));
        if (destroy) {
            dataMap.remove(pid);
        }
    }

    public RankData getRank() {
        RankData rank = RankData.getData("default");
        int priority = -1;
        for (String ranks : RankData.getRanks()) {
            if (RankData.getData(ranks).getPerm().isEmpty()) {
                if (priority == -1 || RankData.getData(ranks).getPriority() < priority) {
                    rank = RankData.getData(ranks);
                    priority = RankData.getData(ranks).getPriority();
                }
            }
            if (Bukkit.getPlayer(getUUID()).hasPermission((RankData.getData(ranks).getPerm()))) {
                if (priority == -1 || RankData.getData(ranks).getPriority() < priority) {
                    rank = RankData.getData(ranks);
                    priority = RankData.getData(ranks).getPriority();
                }
            }
        }
        return rank;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerProfile)) {
            return false;
        }
        PlayerProfile profObj = (PlayerProfile) obj;
        if (this == profObj) {
            return true;
        }
        return profObj.getUUID().equals(this.getUUID()) && profObj.pid.equals(this.pid);
    }


    @Override
    public int hashCode() {
        int result = pid.hashCode();
        result = 17 * result + getUUID().hashCode();
        return result;
    }

}
