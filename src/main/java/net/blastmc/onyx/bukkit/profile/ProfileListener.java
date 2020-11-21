package net.blastmc.onyx.bukkit.profile;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.api.PlayerProfile;
import net.blastmc.onyx.bukkit.api.Onyx;
import net.blastmc.onyx.bukkit.api.event.PlayerInitEvent;
import net.blastmc.onyx.bukkit.proxy.ServerType;
import net.blastmc.onyx.bukkit.util.BukkitMethod;
import net.blastmc.onyx.bukkit.util.TeamAction;
import net.blastmc.onyx.bukkit.util.interact.SoundUtil;
import net.blastmc.onyx.shared.PlayerData;
import net.blastmc.onyx.bukkit.config.SettingConfig;
import net.blastmc.onyx.bukkit.config.TagConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import net.blastmc.spigot.event.AsyncPlayerConnectEvent;
import net.blastmc.spigot.event.AsyncPlayerDisconnectEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;

public class ProfileListener implements Listener {

    public ProfileListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(AsyncPlayerConnectEvent e) {
        //init profile
        try {
            UUID uuid = UUID.fromString((String) Main.getSQL().getData("player_data", "name", e.getName(), "uuid").get(0));
        }catch (Exception ignored){}
        //check uuid
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerData data = PlayerData.init(p.getUniqueId(), p.getName());
        PlayerProfile prof;
        if (data != null) {
            boolean b = Main.getSQL().checkDataExists("player_profile", "pid", data.getPID());
            if (b) {
                //load profile
                prof = new PlayerProfile(data.getPID());
                prof.setPrefix(Main.getVault().getPlayerPrefix(p));
                prof.setSuffix(Main.getVault().getPlayerSuffix(p));
            } else {
                //create new profile
                prof = new PlayerProfile(
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
            if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    PlayerProfile other = PlayerProfile.getDataFromUUID(online.getUniqueId());
                    if (p != online)
                        Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, "100");
                    String otherPriority = "100";
                    if (other != null) {
                        otherPriority = BukkitMethod.getTagPriority(online, other);
                    }
                    Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, otherPriority);
                    //restore tag
                    Main.getNMS().changeNameTag(online, p, "§7[未注册] ", "", TeamAction.CREATE, "100");
                    if (other != null && p != online) {
                        String suffix = PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getSuffix());
                        if (other.isVanish()) {
                            suffix = " §c[已隐身]";
                        }
                        Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, suffix), TeamAction.CREATE, BukkitMethod.getTagPriority(online, other));
                    }
                }
            }
            return;
        }

        if (prof.isVanish()) {
            PlayerProfile.addVanishPlayer(prof);
            for (Player online : Bukkit.getOnlinePlayers()) {
                BukkitMethod.vanishPlayer(p, online, true);
            }
        }

        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerProfile other = PlayerProfile.getDataFromUUID(online.getUniqueId());
                if (other == null) {
                    continue;
                }
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, BukkitMethod.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, BukkitMethod.getTagPriority(online, other));
                //restore tag
                String suffix = PlaceholderAPI.setPlaceholders(p, BukkitMethod.getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                String priority = BukkitMethod.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, BukkitMethod.getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
                if (p != online) {
                    String otherSuffix = PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getSuffix());
                    if (other.isVanish()) {
                        otherSuffix = " §c[已隐身]";
                    }
                    Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, otherSuffix), TeamAction.CREATE, BukkitMethod.getTagPriority(online, other));
                }
            }
        }

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
                Onyx.getApi().refreshTag(p);
            }
        }

        for (PlayerProfile profs : PlayerProfile.getVanishs()) {
            BukkitMethod.vanishPlayer(Bukkit.getPlayer(profs.getUUID()), p, true);
        }

        //join message
        String message = null;
        p.setDisplayName(PlaceholderAPI.setPlaceholders(p, "%profile_prefix%") + p.getName() + PlaceholderAPI.setPlaceholders(p, "%profile_suffix%"));
        if (SettingConfig.cfg.getYml().getBoolean("setting.join-msg-enabled")) {
            if (!prof.isVanish()) {
                message = SettingConfig.cfg.getYml().getString("setting.join-msg")
                        .replace("{displayname}", p.getDisplayName())
                        .replace("{name}", p.getName());
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        online.playSound(p.getLocation(), SoundUtil.NOTE_PLING, 3.0F, 1.3F);
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
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        if(prof == null){
            return;
        }
        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerProfile other = PlayerProfile.getDataFromUUID(online.getUniqueId());
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, BukkitMethod.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, BukkitMethod.getTagPriority(online, PlayerProfile.getDataFromUUID(online.getUniqueId())));
                //restore tag
                String suffix = PlaceholderAPI.setPlaceholders(p, BukkitMethod.getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                String priority = BukkitMethod.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, BukkitMethod.getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
                if (other != null && p != online) {
                    String otherSuffix = PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getSuffix());
                    if (other.isVanish()) {
                        otherSuffix = " §c[已隐身]";
                    }
                    Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, BukkitMethod.getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, otherSuffix), TeamAction.CREATE, BukkitMethod.getTagPriority(online, other));
                }
            }
        }
        for (PlayerProfile other : PlayerProfile.getVanishs()) {
            if (other.isVanish()) {
                BukkitMethod.vanishPlayer(Bukkit.getPlayer(other.getUUID()), p, true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogout(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        if(prof == null){
            return;
        }
        //Tag Init
        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, BukkitMethod.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, BukkitMethod.getTagPriority(online, PlayerProfile.getDataFromUUID(online.getUniqueId())));
            }
        }
        PlayerProfile.removeVanishPlayer(prof);
        if (SettingConfig.cfg.getYml().getBoolean("setting.quit-msg-enabled")) {
            if (!prof.isVanish()) {
                e.setQuitMessage(SettingConfig.cfg.getYml().getString("setting.quit-msg")
                        .replace("{displayname}", p.getDisplayName())
                        .replace("{name}", p.getName()));
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.playSound(p.getLocation(), SoundUtil.NOTE_BASS, 3.0F, 1.0F);
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
    public void onDisconnect(AsyncPlayerDisconnectEvent e){
        PlayerData data = PlayerData.getDataFromUUID(e.getUniqueId());
        if(data != null){
            data.destroy();
        }
    }

    private void checkUUID(AsyncPlayerConnectEvent e, OfflinePlayer off, PlayerData data){
        if(!off.getUniqueId().toString().equals(data.getUUID())){
            e.setUniqueId(data.getUUID());
        }
    }

}
