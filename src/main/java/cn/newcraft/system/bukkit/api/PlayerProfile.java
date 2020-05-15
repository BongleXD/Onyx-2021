package cn.newcraft.system.bukkit.api;

import cn.newcraft.system.bukkit.api.event.PlayerLevelUPEvent;
import cn.newcraft.system.bukkit.api.event.PlayerXpGainEvent;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.rank.RankData;
import cn.newcraft.system.bukkit.util.interact.ActionBarUtil;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.shared.util.SQLHelper;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerProfile {

    private String pid;
    private UUID uuid;
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
    private static HashMap<String, PlayerProfile> dataMap = new HashMap<>();
    private static SQLHelper sql = Main.getSQL();
    public static List<UUID> vanishs = Lists.newArrayList();

    public PlayerProfile(String pid, UUID uuid, int level, int xp, double xpBoost, double coinBoost, boolean vanish, boolean nicked, String nickName, String nickPrefix, String nickSkin, String secondPasswd) {
        this.pid = pid;
        this.uuid = uuid;
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
        dataMap.put(pid, this);
        sql.putFlag("player_profile", "pid", this.pid);
    }

    public PlayerProfile(String pid){
        this.pid = pid;
        putData();
        dataMap.put(pid, this);
    }

    private void putData(){
        this.uuid = UUID.fromString((String) sql.getData("pid", pid, "player_profile", "uuid"));
        this.level = (int) sql.getData("pid", pid, "player_profile", "net_level");
        this.xp = (int) sql.getData("pid", pid, "player_profile", "net_xp");
        this.xpBoost = (double) sql.getData("pid", pid, "player_profile", "xp_boost");
        this.coinBoost = (double) sql.getData("pid", pid, "player_profile", "coin_boost");
        this.vanish = (int) sql.getData("pid", pid, "player_profile", "vanish") != 0;
        this.nicked = (int) sql.getData("pid", pid, "player_profile", "nicked") != 0;
        this.nickName = (String) sql.getData("pid", pid, "player_profile", "nick_name");
        this.nickPrefix = (String) sql.getData("pid", pid, "player_profile", "nick_prefix");
        this.nickSkin = (String) sql.getData("pid", pid, "player_profile", "nick_skin");
        this.secondPasswd = (String) sql.getData("pid", pid, "player_profile", "second_passwd");
    }

    public static void init(){
        sql.create("player_profile");
        sql.addStringColumn("player_profile", "pid");
        sql.addStringColumn("player_profile", "uuid");
        sql.addIntegerColumn("player_profile", "net_level");
        sql.addIntegerColumn("player_profile", "net_xp");
        sql.addDoubleColumn("player_profile", "xp_boost");
        sql.addDoubleColumn("player_profile", "coin_boost");
        sql.addIntegerColumn("player_profile", "vanish");
        sql.addIntegerColumn("player_profile", "nicked");
        sql.addStringColumn("player_profile", "nick_name");
        sql.addStringColumn("player_profile", "nick_prefix");
        sql.addStringColumn("player_profile", "nick_skin");
        sql.addStringColumn("player_profile", "second_passwd");
    }

    public String getPID(){
        return this.pid;
    }

    public UUID getUUID(){
        return this.uuid;
    }

    public void addLevel(int value){
        Bukkit.getPluginManager().callEvent(new PlayerLevelUPEvent(Bukkit.getPlayer(uuid), level, level + value));
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
    }

    public void addXp(int value){
        double d = (double) value * xpBoost;
        value = (int) d;
        Bukkit.getPluginManager().callEvent(new PlayerXpGainEvent(Bukkit.getPlayer(uuid), value, xpBoost));
        this.xp = value + xp;
        checkLevelUp();
    }

    public void checkLevelUp(){
        if(this.xp >= getXpToLevelUp()){
            this.xp = xp - getXpToLevelUp();
            addLevel(1);
            checkLevelUp();
        }
    }

    public int getXpToLevelUp(){
        return 10000 + (level * 2500);
    }

    public String getProgressBar(){
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

    public void checkStatus(){
        if((isVanish() && isNicked() && Main.getGameManager() != null) || (isVanish() && isNicked() && Bukkit.getPlayer(uuid).hasPermission("ncs.nick.stuff"))){
            ActionBarUtil.cancel(Bukkit.getPlayer(uuid));
            ActionBarUtil.sendBar(Bukkit.getPlayer(uuid), "§f§l你目前§c§l已设置昵称, 隐身", -1);
        }else if(isVanish()){
            ActionBarUtil.cancel(Bukkit.getPlayer(uuid));
            ActionBarUtil.sendBar(Bukkit.getPlayer(uuid), "§f§l你目前§c§l已隐身", -1);
        }else if((isNicked() && Main.getGameManager() != null) || (isNicked() && Bukkit.getPlayer(uuid).hasPermission("ncs.nick.stuff"))){
            ActionBarUtil.cancel(Bukkit.getPlayer(uuid));
            ActionBarUtil.sendBar(Bukkit.getPlayer(uuid), "§f§l你目前§c§l已设置昵称", -1);
        }else{
            ActionBarUtil.cancel(Bukkit.getPlayer(uuid));
        }
    }

    public static PlayerProfile getData(String pid){
        return dataMap.getOrDefault(pid, null);
    }

    public static PlayerProfile getDataFromUUID(UUID uuid){
        return dataMap.getOrDefault(sql.getData("uuid", uuid.toString(), "player_data", "pid"), null);
    }

    public void saveData(boolean destroy){
        sql.putData("player_profile", this.pid, "uuid", this.uuid.toString());
        sql.putData("player_profile", this.pid, "net_level", this.level);
        sql.putData("player_profile", this.pid, "net_xp", this.xp);
        sql.putData("player_profile", this.pid, "xp_boost", this.xpBoost);
        sql.putData("player_profile", this.pid, "coin_boost", this.coinBoost);
        sql.putData("player_profile", this.pid, "vanish", this.vanish ? 1 : 0);
        sql.putData("player_profile", this.pid, "nicked", this.nicked ? 1 : 0);
        sql.putData("player_profile", this.pid, "nick_name", this.nickName);
        sql.putData("player_profile", this.pid, "nick_prefix", this.nickPrefix);
        sql.putData("player_profile", this.pid, "nick_skin", this.nickSkin);
        sql.putData("player_profile", this.pid, "second_passwd", this.secondPasswd);
        if(destroy){
            dataMap.remove(pid);
        }
    }

    public RankData getRank(){
        RankData rank = RankData.getData("default");
        int priority = -1;
        for(String ranks : RankData.getRanks()){
            if(RankData.getData(ranks).getPerm().isEmpty()){
                if(priority == -1 || RankData.getData(ranks).getPriority() < priority) {
                    rank = RankData.getData(ranks);
                    priority = RankData.getData(ranks).getPriority();
                }
            }
            if(Bukkit.getPlayer(uuid).hasPermission((RankData.getData(ranks).getPerm()))){
                if(priority == -1 || RankData.getData(ranks).getPriority() < priority) {
                    rank = RankData.getData(ranks);
                    priority = RankData.getData(ranks).getPriority();
                }
            }
        }
        return rank;
    }

}
