package cn.newcraft.system.bukkit.api.event;

import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.gamemanager.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRejoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player p;
    private Team team;
    private PlayerData data;

    public PlayerRejoinEvent(Player p, Team team) {
        this.p = p;
        this.data = PlayerData.getDataFromUUID(p.getUniqueId());
        this.team = team;
    }

    public Player getPlayer() {
        return p;
    }

    public Team getTeam() {
        return team;
    }

    public PlayerData getData() {
        return data;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
