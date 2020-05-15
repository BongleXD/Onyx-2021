package cn.newcraft.system.bukkit.gamemanager;

import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.event.PlayerRejoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

public class ServerListener implements Listener {

    public ServerListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (Main.getGameManager() != null) {
            if(Main.getGameManager().getStatus() == ServerStatus.MAINTENANCE){
                return;
            }
            if(Main.getGameManager().getOffline().contains(p.getUniqueId())) {
                Main.getGameManager().remOffline(PlayerData.getDataFromUUID(p.getUniqueId()).getPID());
                Team team = null;
                for(Team all : Main.getGameManager().getTeams()){
                    String pid = PlayerData.getDataFromUUID(e.getPlayer().getUniqueId()).getPID();
                    if(Arrays.asList(all.getPids()).contains(pid)){
                        team = all;
                       break;
                    }
                }
                Bukkit.getPluginManager().callEvent(new PlayerRejoinEvent(e.getPlayer(), team));
            }
            Main.getGameManager().addOnline(PlayerData.getDataFromUUID(e.getPlayer().getUniqueId()).getPID());
            if(Bukkit.getOnlinePlayers().size() >= Main.getGameManager().getMaxPlayer() + Main.getGameManager().getSpec()){
                Main.getGameManager().setStatus(ServerStatus.FULL);
            }
            if(Main.getGameManager().getType() == ServerType.ROOM) {
                if(Main.getGameManager().getGameStatus() == GameStatus.GAME_START) {
                    if (Bukkit.getOnlinePlayers().size() >= Main.getGameManager().getMaxPlayer()) {
                        Main.getGameManager().setStatus(ServerStatus.SPECABLE);
                    }
                }else {
                    if (Bukkit.getOnlinePlayers().size() >= Main.getGameManager().getStartAtLeast()) {
                        Main.getGameManager().setStatus(ServerStatus.STARTING);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        if (Main.getGameManager() != null) {
            if (Main.getGameManager().getStatus() == ServerStatus.MAINTENANCE) {
                return;
            }
            Main.getGameManager().remOnline(PlayerData.getDataFromUUID(e.getPlayer().getUniqueId()).getPID());
            Main.getGameManager().addOffline(PlayerData.getDataFromUUID(e.getPlayer().getUniqueId()).getPID());
            switch (Main.getGameManager().getType()) {
                case ENDLESS:
                    Main.getGameManager().setStatus(ServerStatus.JOINABLE);
                    break;
                case ROOM:
                    if (Main.getGameManager().getGameStatus() == GameStatus.NULL) {
                        if (Main.getGameManager().getStartAtLeast() <= Bukkit.getOnlinePlayers().size()) {
                            Main.getGameManager().setStatus(ServerStatus.STARTING);
                        } else {
                            Main.getGameManager().setStatus(ServerStatus.WAITING);
                        }
                    } else if (Main.getGameManager().getGameStatus() == GameStatus.GAME_START) {
                        Main.getGameManager().setStatus(ServerStatus.PLAYING);
                    } else {
                        Main.getGameManager().setStatus(ServerStatus.ENDING);
                    }
                    break;
            }
        }
    }

}
