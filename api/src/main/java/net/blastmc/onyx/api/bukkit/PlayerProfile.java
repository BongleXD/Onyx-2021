package net.blastmc.onyx.api.bukkit;

import java.util.UUID;

public interface PlayerProfile {

    String getPID();

    UUID getUUID();

    void addLevel(int value);

    double getXpBoost();

    void setXpBoost(double xpBoost);

    int getLevel();

    void setLevel(int level);

    int getXp();

    void setXp(int xp);

    void addXp(int xp);

    void addXpWithoutCallEvent(int value);

    void checkLevelUp();

    String getPrefix();

    void setPrefix(String prefix);

    String getSuffix();

    void setSuffix(String suffix);

    int getXpToLevelUp();

    String getProgressBar();

    double getCoinBoost();

    void setCoinBoost(double coinBoost);

    boolean isVanish();

    void setVanish(boolean vanish);

    boolean isNicked();

    void setNicked(boolean nicked);

    String getNickName();

    void setNickName(String nickName);

    String getNickPrefix();

    void setNickPrefix(String nickPrefix);

    String getNickSkin();

    void setNickSkin(String nickSkin);

    String getSecondPasswd();

    void setSecondPasswd(String secondPasswd);

    void checkStatus();

    void saveData(boolean destroy);

    Rank getRank();

}
