package cn.newcraft.system.bukkit.gamemanager;

import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.event.GameStatusChangeEvent;
import cn.newcraft.system.bukkit.api.event.ServerStatusChangeEvent;
import cn.newcraft.system.shared.util.SQLHelper;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameManager {

    private static SQLHelper sql = Main.getSQL();
    private ServerType type;
    private String gameID;
    private String mapID;
    private ServerStatus status = ServerStatus.OFFLINE;
    private GameStatus gameStatus = GameStatus.NULL;
    private int maxPlayer;
    private int startAtLeast;
    private int spec;
    private List<Team> teams = new ArrayList<>();
    private List<String> online = new ArrayList<>();
    private List<String> offline = new ArrayList<>();

    public GameManager(ServerType type) {
        this.type = type;
    }

    private static void create(){
        sql.create("server_info");
        sql.addStringColumn("server_info", "name");
        sql.addStringColumn("server_info", "server_status");
        sql.addStringColumn("server_info", "game_status");
        sql.addIntegerColumn("server_info", "max_player");
        sql.addIntegerColumn("server_info", "start_at_least");
        sql.addStringColumn("server_info", "game_id");
        sql.addStringColumn("server_info", "map_id");
        sql.addIntegerColumn("server_info", "spec_max");
        sql.addStringColumn("server_info", "server_type");
    }

    public static GameManager init(){
        create();
        if(!sql.checkDataExists("server_info", "name", Main.getServerName())) {
            sql.putFlag("server_info", "name", Main.getServerName());
            GameManager gm = new GameManager(ServerType.ENDLESS);
            gm.status = ServerStatus.JOINABLE;
            gm.maxPlayer = Bukkit.getServer().getMaxPlayers();
            gm.gameStatus = GameStatus.NULL;
            gm.gameID = "NULL";
            gm.mapID = "NULL";
            gm.spec = 0;
            gm.startAtLeast = 0;
            gm.saveData(true);
            Bukkit.getConsoleSender().sendMessage("§bGameManager §7> §a房间初始化完成！");
            return gm;
        }else{
            ServerType type = ServerType.valueOf((String) sql.getData("name", Main.getServerName(), "server_info", "server_type"));
            GameManager gm = new GameManager(type);
            gm.maxPlayer = (Integer) sql.getData("name", Main.getServerName(), "server_info", "max_player");
            if(type == ServerType.ROOM) {
                gm.setStatus(ServerStatus.WAITING);
                gm.gameID = (String) sql.getData("name", Main.getServerName(), "server_info", "game_id");
                gm.mapID = (String) sql.getData("name", Main.getServerName(), "server_info", "map_id");
                gm.spec = (int) sql.getData("name", Main.getServerName(), "server_info", "spec_max");
                gm.startAtLeast = (int) sql.getData("name", Main.getServerName(), "server_info", "start_at_least");
            }else{
                gm.setStatus(ServerStatus.JOINABLE);
                gm.gameID = (String) sql.getData("name", Main.getServerName(), "server_info", "game_id");
                gm.mapID = "NULL";
                gm.spec = 0;
                gm.startAtLeast = 0;
            }
            Bukkit.getConsoleSender().sendMessage("§bGameManager §7> §a房间数据导入成功！");
            return gm;
        }
    }

    public GameStatus getGameStatus(){
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        Bukkit.getPluginManager().callEvent(new GameStatusChangeEvent(this, this.gameStatus, gameStatus));
        this.gameStatus = gameStatus;
        new BukkitRunnable(){
            @Override
            public void run() {
                sql.putData("server_info", "name", Main.getServerName(), "game_status", gameStatus.name());
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        Bukkit.getPluginManager().callEvent(new ServerStatusChangeEvent(this, this.status, status));
        this.status = status;
        new BukkitRunnable(){
            @Override
            public void run() {
                sql.putData("server_info", "name", Main.getServerName(), "server_status", status.name());
            }
        }.runTaskAsynchronously(Main.getInstance());
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void addTeam(Team team){
        teams.add(team);
    }

    public void remTeam(String teamName){
        teams.removeIf(team -> teamName.equalsIgnoreCase(team.getTeamName()));
    }

    public List<UUID> getOnline() {
        List<UUID> players = new ArrayList<>();
        online.forEach(pid -> {
            players.add(UUID.fromString(PlayerData.getData(pid).getUUID()));
        });
        return players;
    }

    public void addOnline(String pid){
        online.add(pid);
    }

    public void remOnline(String pid){
        online.remove(pid);
    }

    public List<UUID> getOffline() {
        List<UUID> players = new ArrayList<>();
        offline.forEach(pid -> {
            players.add(UUID.fromString(PlayerData.getData(pid).getUUID()));
        });
        return players;
    }

    public void addOffline(String pid){
        offline.add(pid);
    }

    public void remOffline(String pid){
        offline.remove(pid);
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public int getStartAtLeast() {
        return startAtLeast;
    }

    public void setStartAtLeast(int startAtLeast) {
        this.startAtLeast = startAtLeast;
    }

    public ServerType getType() {
        return type;
    }

    public int getSpec() {
        return spec;
    }

    public void setSpec(int spec) {
        this.spec = spec;
    }

    public String getGameID() {
        return gameID;
    }

    public String getMapID() {
        return mapID;
    }

    public void saveData(boolean isFirst){
        if(isFirst) {
            sql.putData("server_info", "name", Main.getServerName(), "server_status", this.status.name());
            sql.putData("server_info", "name", Main.getServerName(), "game_status", this.gameStatus.name());
        }else{
            sql.putData("server_info", "name", Main.getServerName(), "server_status", "OFFLINE");
            sql.putData("server_info", "name", Main.getServerName(), "game_status", "NULL");
        }
        sql.putData("server_info", "name", Main.getServerName(), "game_id", this.gameID);
        sql.putData("server_info", "name", Main.getServerName(), "map_id", this.mapID);
        sql.putData("server_info", "name", Main.getServerName(), "max_player", this.maxPlayer);
        sql.putData("server_info", "name", Main.getServerName(), "spec_max", this.spec);
        sql.putData("server_info", "name", Main.getServerName(), "start_at_least", this.startAtLeast);
        sql.putData("server_info", "name", Main.getServerName(), "server_type", this.type.name());
    }

}
