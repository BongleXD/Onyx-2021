package cn.newcraft.system.bungee.config;

import cn.newcraft.system.bungee.Main;

import java.io.File;
import java.util.List;

public class LobbyConfig extends ConfigManager {

    public static LobbyConfig cfg;

    public LobbyConfig() {
        super("lobby");
    }
    public static void init(){
        LobbyConfig.cfg = new LobbyConfig();
        List<String> mainList = cfg.getYml().getStringList("Lobby.MainLobby");
        mainList.add("Lobby_01");
        mainList.add("Lobby_02");
        cfg.getYml().set("Lobby.MainLobby", mainList);

        List<String> skyWarsList = cfg.getYml().getStringList("Lobby.SkyWarsLobby");
        skyWarsList.add("SWLOBBY_01");
        cfg.getYml().set("Lobby.SkyWarsLobby", skyWarsList);

        List<String> bedWarsList = cfg.getYml().getStringList("Lobby.BedWarsLobby");
        bedWarsList.add("BWLOBBY_01");
        cfg.getYml().set("Lobby.BedWarsLobby", bedWarsList);
        cfg.save();
    }
}
