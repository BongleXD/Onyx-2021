package net.blastmc.onyx.api;

import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.Rank;
import net.blastmc.onyx.api.bukkit.server.ServerType;
import net.blastmc.onyx.api.util.SQLHelper;

import java.util.UUID;

public interface API {

    PlayerProfile getPlayerProfile(UUID uuid);

    PlayerProfile getPlayerProfile(String name);

    PlayerProfile getPlayerProfileFromPID(String pid);

    PlayerData getPlayerData(UUID uuid);

    PlayerData getPlayerData(String name);

    PlayerData getPlayerDataFromPID(String pid);

    void refreshAllPlayerVanish();

    void refreshVanish(UUID uuid);

    String getServerName();

    ServerType getType();

    void kickToLobby(UUID uuid, String server, String name, String reason);

    void refreshTag(UUID uuid);

    void removeTag(UUID uuid);

    void refreshAllTag();

    void refreshTagFor(UUID uuid);

    SQLHelper getSQL();

    String getPIDIgnoreNick(String name);

    UUID getOfflineUUID(String pid);

    String getOfflineName(String pid);

    String getOfflinePID(UUID uuid);

    String getOfflinePID(String name);

    boolean isBanned(String pid);

    String getOfflineTrueDisplayName(UUID uuid);

    void createNewData(String pid, UUID uuid, String name);

    void createNewData(UUID uuid, String name);

    String spawnUniquePID();

    String getAPIVersion();

    Rank getRank(String name);

}
