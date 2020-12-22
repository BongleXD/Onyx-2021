package net.blastmc.onyx.bukkit.profile;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.PlayerData;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.server.ServerType;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.api.bukkit.event.PlayerInitEvent;
import net.blastmc.onyx.bukkit.api.OnyxPlayerProfile;
import net.blastmc.onyx.bukkit.command.admin.Vanish;
import net.blastmc.onyx.bukkit.util.Method;
import net.blastmc.onyx.bukkit.util.TeamAction;
import net.blastmc.onyx.bukkit.util.interact.SoundUtil;
import net.blastmc.onyx.bukkit.config.SettingConfig;
import net.blastmc.onyx.bukkit.config.TagConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class ProfileListener implements Listener {

    public ProfileListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }
/*
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(AsyncPlayerConnectEvent e) {
        //init profile
        try {
            UUID uuid = UUID.fromString((String) Main.getSQL().getData("player_data", "name", e.getName(), "uuid").get(0));
        }catch (Exception ignored){}
        //check uuid
    }

 */

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerData data = Onyx.init(p.getUniqueId(), p.getName());
        PlayerProfile prof;
        if (data != null) {
            boolean b = Main.getSQL().checkDataExists("player_profile", "pid", data.getPID());
            if (b) {
                //load profile
                prof = new OnyxPlayerProfile(data.getPID());
                prof.setPrefix(Main.getVault().getPlayerPrefix(p));
                prof.setSuffix(Main.getVault().getPlayerSuffix(p));
            } else {
                //create new profile
                prof = new OnyxPlayerProfile(
                        data.getPID(),
                        1,
                        0,
                        1.0,
                        1.0,
                        false,
                        false,
                        "",
                        "",
                        "",
                        "",
                        Main.getVault().getPlayerPrefix(p),
                        Main.getVault().getPlayerSuffix(p)
                );
                prof.saveData(false);
            }
        } else {
            if (TagConfig.ENABLED && TagConfig.ENABLED_WORLD.contains(p.getWorld().getName())) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    PlayerProfile other = Onyx.getPlayerProfile(online.getUniqueId());
                    if (p != online)
                        Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, "100");
                    String otherPriority = "100";
                    if (other != null) {
                        otherPriority = Method.getTagPriority(online, other);
                    }
                    Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, otherPriority);
                    //restore tag
                    Main.getNMS().changeNameTag(online, p, "§7[未注册] ", "", TeamAction.CREATE, "100");
                    if (other != null && p != online) {
                        String suffix = PlaceholderAPI.setPlaceholders(online, Method.getTagData(online).getSuffix());
                        if (other.isVanish()) {
                            suffix = " §c[已隐身]";
                        }
                        Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, Method.getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, suffix), TeamAction.CREATE, Method.getTagPriority(online, other));
                    }
                }
            }
            return;
        }

        if (prof.isVanish()) {
            Vanish.vanishList.add(prof);
            for (Player online : Bukkit.getOnlinePlayers()) {
                Method.vanishPlayer(p, online, true);
            }
        }

        Onyx.getAPI().refreshTagFor(p.getUniqueId());

        //nick check
        if (((prof.isNicked() && (Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME)) || (prof.isNicked() && p.hasPermission("onyx.nick.staff"))) && !Main.getInstance().getConfig().getBoolean("disable-nick")) {
            if (!Main.getSQL().checkDataExists("player_data", "name", prof.getNickName())) {
                Main.getNMS().changeName(p, prof.getNickName());
            } else {
                prof.setNicked(false);
                prof.setNickSkin("");
                prof.setNickName("");
                prof.setNickPrefix("");
                prof.saveData(false);
                p.sendMessage("§c有玩家使用了此昵称所以已经将你的昵称还原！");
                Onyx.getAPI().refreshTag(p.getUniqueId());
            }
        }

        for (PlayerProfile profs : Vanish.vanishList) {
            Method.vanishPlayer(Bukkit.getPlayer(profs.getUUID()), p, true);
        }

        //join message
        String message = null;
        p.setDisplayName(PlaceholderAPI.setPlaceholders(p, "%profile_prefix%") + p.getName() + PlaceholderAPI.setPlaceholders(p, "%profile_suffix%"));
        if (SettingConfig.JOIN_MSG_ENABLED) {
            if (!prof.isVanish()) {
                message = SettingConfig.JOIN_MSG
                        .replace("{displayname}", p.getDisplayName())
                        .replace("{name}", p.getName());
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        online.playSound(p.getLocation(), SoundUtil.NOTE_PLING.getSound(), 3.0F, 1.3F);
                    }, 5L);
                }
            }
        }
        PlayerInitEvent event = new PlayerInitEvent(p, message);
        Bukkit.getPluginManager().callEvent(event);
        e.setJoinMessage(event.getJoinMessage());
        prof.checkStatus();
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        Onyx.getAPI().refreshTagFor(p.getUniqueId());
        for (PlayerProfile other : Vanish.vanishList) {
            if (other.isVanish()) {
                Method.vanishPlayer(Bukkit.getPlayer(other.getUUID()), p, true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogout(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
        if(prof == null){
            return;
        }
        //Tag Init
        if (TagConfig.ENABLED && TagConfig.ENABLED_WORLD.contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, Method.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, Onyx.getPlayerProfile(online.getUniqueId())));
            }
        }
        Vanish.vanishList.remove(prof);
        if (SettingConfig.QUIT_MSG_ENABLED) {
            if (!prof.isVanish()) {
                e.setQuitMessage(SettingConfig.QUIT_MSG
                        .replace("{displayname}", p.getDisplayName())
                        .replace("{name}", p.getName()));
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.playSound(p.getLocation(), SoundUtil.NOTE_BASS.getSound(), 3.0F, 1.0F);
                }
            } else {
                e.setQuitMessage(null);
            }
        } else {
            e.setQuitMessage(null);
        }
        prof.saveData(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e){
        PlayerData data = Onyx.getPlayerData(e.getPlayer().getUniqueId());
        if(data != null){
            data.saveData(true);
        }
    }

    /*
    private void checkUUID(AsyncPlayerConnectEvent e, OfflinePlayer off, PlayerData data){
        if(!off.getUniqueId().toString().equals(data.getUUID())){
            e.setUniqueId(data.getUUID());
        }
    }

     */

}
