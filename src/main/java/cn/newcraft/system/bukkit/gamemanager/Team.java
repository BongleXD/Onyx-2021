package cn.newcraft.system.bukkit.gamemanager;

import cn.newcraft.system.shared.PlayerData;

import java.util.Arrays;
import java.util.UUID;

public class Team {

    private String[] pids;
    private int maxPlayer;
    private String teamName;

    public Team(int maxPlayer, String teamName) {
        this.maxPlayer = maxPlayer;
        this.teamName = teamName;
    }

    public String[] getPids() {
        return pids;
    }

    public void addPlayer(UUID uuid){
        if(pids.length < maxPlayer) {
            Arrays.asList(pids).add(PlayerData.getDataFromUUID(uuid).getPID());
        }
    }

    public String getTeamName() {
        return teamName;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

}
