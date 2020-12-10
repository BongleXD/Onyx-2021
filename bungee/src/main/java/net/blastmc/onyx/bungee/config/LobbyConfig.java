package net.blastmc.onyx.bungee.config;

import com.google.common.collect.Lists;

import java.util.List;

public class LobbyConfig extends ConfigManager {

    public static LobbyConfig cfg;

    public LobbyConfig() {
        super("lobby");
    }

    public static void init(){
        LobbyConfig.cfg = new LobbyConfig();
        List<String> mainList = Lists.newArrayList();
        mainList.add("mainlobby01");
        mainList.add("mainlobby02");
        cfg.addDefault("lobby.mainLobby", mainList);

        List<String> skyWarsList = Lists.newArrayList();
        skyWarsList.add("swlobby01");
        cfg.addDefault("lobby.swLobby", skyWarsList);

        List<String> bedWarsList = Lists.newArrayList();
        bedWarsList.add("bwlobby01");
        cfg.addDefault("lobby.bwLobby", bedWarsList);
        cfg.save();

    }
}
