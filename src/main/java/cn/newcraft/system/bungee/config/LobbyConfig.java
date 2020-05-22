package cn.newcraft.system.bungee.config;

import cn.newcraft.system.bungee.Main;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

public class LobbyConfig extends ConfigManager {

    public static LobbyConfig cfg;

    public LobbyConfig() {
        super("lobby");
    }

    public static void init(){
        LobbyConfig.cfg = new LobbyConfig();
        List<String> mainList = Lists.newArrayList();
        mainList.add("Lobby_01");
        mainList.add("Lobby_02");
        cfg.addDefault("lobby.mainLobby", mainList);

        List<String> skyWarsList = Lists.newArrayList();
        skyWarsList.add("SWLOBBY_01");
        cfg.addDefault("lobby.swLobby", skyWarsList);

        List<String> bedWarsList = Lists.newArrayList();
        bedWarsList.add("BWLOBBY_01");
        cfg.addDefault("lobby.bwLobby", bedWarsList);
        cfg.save();

    }
}
