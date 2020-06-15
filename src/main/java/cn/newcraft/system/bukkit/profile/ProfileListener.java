package cn.newcraft.system.bukkit.profile;

import cn.newcraft.spigot.event.PlayerPreConnectEvent;
import cn.newcraft.system.bukkit.api.event.PlayerInitEvent;
import cn.newcraft.system.bukkit.proxy.ServerType;
import cn.newcraft.system.bukkit.util.interact.SoundUtil;
import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.api.SystemAPI;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.config.SettingConfig;
import cn.newcraft.system.bukkit.config.TagConfig;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.bukkit.util.TeamAction;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;

import static cn.newcraft.system.bukkit.util.Method.getTagData;
import static cn.newcraft.system.bukkit.util.Method.vanishPlayer;

public class ProfileListener implements Listener {

    public ProfileListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerPreConnectEvent e) {
        //init profile
        try {
            UUID uuid = UUID.fromString((String) Main.getSQL().getData("player_data", "player_name", e.getName(), "uuid").get(0));
        }catch (Exception ignored){}
        //check uuid
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for (UUID uuid : PlayerProfile.getVanishs()) {
            PlayerProfile prof = PlayerProfile.getDataFromUUID(uuid);
            if (prof.isVanish()) {
                vanishPlayer(Bukkit.getPlayer(uuid), p, true);
            }
        }
        PlayerData data = PlayerData.init(p.getUniqueId(), p.getName());
        PlayerProfile prof;
        if (data != null) {
            boolean b = Main.getSQL().checkDataExists("player_profile", "uuid", p.getUniqueId().toString());
            if (b) {
                //load profile
                prof = new PlayerProfile(data.getPID());
            } else {
                //create new profile
                prof = new PlayerProfile(
                        data.getPID(),
                        p.getUniqueId(),
                        1,
                        0,
                        1.0,
                        1.0,
                        false,
                        false,
                        "",
                        "",
                        "",
                        ""
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
                        otherPriority = Method.getTagPriority(online, other);
                    }
                    Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, otherPriority);
                    //restore tag
                    Main.getNMS().changeNameTag(online, p, "§7[未注册] ", "", TeamAction.CREATE, "100");
                    if (other != null && p != online) {
                        String suffix = PlaceholderAPI.setPlaceholders(online, getTagData(online).getSuffix());
                        if (other.isVanish()) {
                            suffix = " §c[已隐身]";
                        }
                        Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, suffix), TeamAction.CREATE, Method.getTagPriority(online, other));
                    }
                }
            }
            return;
        }

        if (prof.isVanish()) {
            PlayerProfile.addVanishPlayer(p.getUniqueId());
            for (Player online : Bukkit.getOnlinePlayers()) {
                vanishPlayer(p, online, true);
            }
        }

        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                PlayerProfile other = PlayerProfile.getDataFromUUID(online.getUniqueId());
                if (other == null) {
                    continue;
                }
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, Method.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, other));
                //restore tag
                String suffix = PlaceholderAPI.setPlaceholders(p, getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                String priority = Method.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
                if (p != online) {
                    String otherSuffix = PlaceholderAPI.setPlaceholders(online, getTagData(online).getSuffix());
                    if (other.isVanish()) {
                        otherSuffix = " §c[已隐身]";
                    }
                    Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, otherSuffix), TeamAction.CREATE, Method.getTagPriority(online, other));
                }
            }
        }

        //nick check
        if (((prof.isNicked() && (Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME)) || (prof.isNicked() && p.hasPermission("ncs.nick.staff"))) && !Main.getInstance().getConfig().getBoolean("disable-nick")) {
            if (!Main.getSQL().checkDataExists("player_data", "player_name", prof.getNickName())) {
                Main.getNMS().changeName(p, prof.getNickName());
            } else {
                prof.setNicked(false);
                prof.setNickSkin("");
                prof.setNickName("");
                prof.setNickPrefix("");
                prof.saveData(false);
                p.sendMessage("§c有玩家使用了此昵称所以已经将你的昵称还原！");
                SystemAPI.getApi().refreshTag(p);
            }
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
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, Method.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, PlayerProfile.getDataFromUUID(online.getUniqueId())));
                //restore tag
                String suffix = PlaceholderAPI.setPlaceholders(p, getTagData(p).getSuffix());
                if (prof.isVanish()) {
                    suffix = " §c[已隐身]";
                }
                String priority = Method.getTagPriority(p, prof);
                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
                if (other != null && p != online) {
                    String otherSuffix = PlaceholderAPI.setPlaceholders(online, getTagData(online).getSuffix());
                    if (other.isVanish()) {
                        otherSuffix = " §c[已隐身]";
                    }
                    Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, otherSuffix), TeamAction.CREATE, Method.getTagPriority(online, other));
                }
            }
        }
        for (UUID uuid : PlayerProfile.getVanishs()) {
            PlayerProfile profile = PlayerProfile.getDataFromUUID(uuid);
            if (profile.isVanish()) {
                vanishPlayer(Bukkit.getPlayer(uuid), p, true);
            }
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        PlayerProfile profile = PlayerProfile.getDataFromUUID(p.getUniqueId());
        if(profile == null){
            return;
        }
        //Tag Init
        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, Method.getTagPriority(p, profile));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, PlayerProfile.getDataFromUUID(online.getUniqueId())));
            }
        }
        PlayerProfile.removeVanishPlayer(p.getUniqueId());
        if (SettingConfig.cfg.getYml().getBoolean("setting.quit-msg-enabled")) {
            if (!profile.isVanish()) {
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
        profile.saveData(true);
    }

    private void checkUUID(PlayerPreConnectEvent e, OfflinePlayer off, PlayerData data){
        if(!off.getUniqueId().toString().equals(data.getUUID())){
            e.setUniqueId(UUID.fromString(data.getUUID()));
        }
    }

}
