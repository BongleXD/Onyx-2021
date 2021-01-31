package net.blastmc.onyx.bukkit.api.impl;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.Rank;
import net.blastmc.onyx.api.bukkit.event.PlayerLevelUPEvent;
import net.blastmc.onyx.api.bukkit.event.PlayerXpGainEvent;
import net.blastmc.onyx.api.bukkit.server.ServerType;
import net.blastmc.onyx.bukkit.util.interact.ActionBarUtil;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.api.util.Method;
import net.blastmc.onyx.api.util.SQLHelper;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

public class OnyxPlayerProfile implements PlayerProfile {

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
    private static SQLHelper sql = Main.getSQL();

    public OnyxPlayerProfile(String pid, int level, int xp, double xpBoost, double coinBoost, boolean vanish, boolean nicked, String nickName, String nickPrefix, String nickSkin, String secondPasswd, String prefix, String suffix) {
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
        BukkitImpl.dataMap.put(pid, this);
    }

    public OnyxPlayerProfile(String pid) {
        this.pid = pid;
        putData();
        BukkitImpl.dataMap.put(pid, this);
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

    @Override
    public String getPID() {
        return this.pid;
    }

    @Override
    public UUID getUUID() {
        return Onyx.getPlayerDataFromPID(pid).getUUID();
    }

    @Override
    public void addLevel(int value) {
        Bukkit.getPluginManager().callEvent(new PlayerLevelUPEvent(Bukkit.getPlayer(getUUID()), level, level + value));
        this.level = this.level + value;
    }

    @Override
    public double getXpBoost() {
        return xpBoost;
    }

    @Override
    public void setXpBoost(double xpBoost) {
        this.xpBoost = xpBoost;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getXp() {
        return xp;
    }

    @Override
    public void setXp(int xp) {
        this.xp = xp;
        checkLevelUp();
    }

    @Override
    public void addXp(int value) {
        double d = (double) value * xpBoost;
        value = (int) d;
        Bukkit.getPluginManager().callEvent(new PlayerXpGainEvent(Bukkit.getPlayer(getUUID()), value, xpBoost));
        this.xp = value + xp;
        checkLevelUp();
    }

    @Override
    public void addXpWithoutCallEvent(int value) {
        double d = (double) value * xpBoost;
        value = (int) d;
        this.xp = value + xp;
        checkLevelUp();
    }

    @Override
    public void checkLevelUp() {
        if (this.xp >= getXpToLevelUp()) {
            this.xp = xp - getXpToLevelUp();
            addLevel(1);
            checkLevelUp();
        }
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public int getXpToLevelUp() {
        return 10000 + (level * 2500);
    }

    @Override
    public String getProgressBar() {
        return Method.getProgressBar(xp, getXpToLevelUp(), 20, "■", "§b", "§7");
    }

    @Override
    public double getCoinBoost() {
        return coinBoost;
    }

    @Override
    public void setCoinBoost(double coinBoost) {
        this.coinBoost = coinBoost;
    }

    @Override
    public boolean isVanish() {
        return vanish;
    }

    @Override
    public void setVanish(boolean vanish) {
        this.vanish = vanish;
        checkStatus();
    }

    @Override
    public boolean isNicked() {
        return nicked;
    }

    @Override
    public void setNicked(boolean nicked) {
        this.nicked = nicked;
        checkStatus();
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String getNickPrefix() {
        return nickPrefix;
    }

    @Override
    public void setNickPrefix(String nickPrefix) {
        this.nickPrefix = nickPrefix;
    }

    @Override
    public String getNickSkin() {
        return nickSkin;
    }

    @Override
    public void setNickSkin(String nickSkin) {
        this.nickSkin = nickSkin;
    }

    @Override
    public String getSecondPasswd() {
        return secondPasswd;
    }

    @Override
    public void setSecondPasswd(String secondPasswd) {
        this.secondPasswd = secondPasswd;
    }

    @Override
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

    @Override
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
            BukkitImpl.dataMap.remove(pid);
        }
    }

    @Override
    public Rank getRank() {
        Rank rank = Onyx.getRank("default");
        int priority = -1;
        for (String ranks : OnyxRank.getRanks()) {
            if (OnyxRank.getData(ranks).getPerm().isEmpty()) {
                if (priority == -1 || Onyx.getRank(ranks).getPriority() < priority) {
                    rank = Onyx.getRank(ranks);
                    priority = Onyx.getRank(ranks).getPriority();
                }
            }
            if (Bukkit.getPlayer(getUUID()).hasPermission((Onyx.getRank(ranks).getPerm()))) {
                if (priority == -1 || Onyx.getRank(ranks).getPriority() < priority) {
                    rank = Onyx.getRank(ranks);
                    priority = Onyx.getRank(ranks).getPriority();
                }
            }
        }
        return rank;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OnyxPlayerProfile)) {
            return false;
        }
        OnyxPlayerProfile profObj = (OnyxPlayerProfile) obj;
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
