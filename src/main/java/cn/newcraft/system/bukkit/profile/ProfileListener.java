package cn.newcraft.system.bukkit.profile;

import cn.newcraft.newcraftspigot.event.PlayerPreConnectEvent;
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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static cn.newcraft.system.bukkit.util.Method.getTagData;
import static cn.newcraft.system.bukkit.util.Method.vanishPlayer;

public class ProfileListener implements Listener {

    public ProfileListener(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerPreConnectEvent e) {
            new BukkitRunnable(){
                @Override
                public void run() {
                    //init profile
                    PlayerData.checkDataFromName(e.getName());
                    PlayerData data = PlayerData.getDataFromName(e.getName());
                    //checkUUID(e, off, data);
                    if(data != null) {
                        boolean b = Main.getSQL().checkDataExists("player_profile", "uuid", e.getUniqueId().toString());
                        if (b) {
                            //load profile
                            new PlayerProfile(data.getPID());
                        } else {
                            //create new profile
                            PlayerProfile profile = new PlayerProfile(
                                    data.getPID(),
                                    e.getUniqueId(),
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
                            profile.saveData(false);
                        }
                    }
                }
            }.runTask(Main.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                synchronized (this){
                    //check data exists
                    boolean b = Main.getSQL().checkDataExists("player_data", "uuid", p.getUniqueId().toString());
                    if(!b){
                        this.cancel();
                        return;
                    }
                    String pid = SystemAPI.getApi().getPID(p.getUniqueId());
                    PlayerProfile prof = PlayerProfile.getData(pid);
                    if(prof == null){
                        return;
                    }

                    //tag init
                    if(TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())){
                        for(Player online : Bukkit.getOnlinePlayers()){
                            if (p != online)
                                Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, Method.getTagPriority(p, prof));
                            Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, PlayerProfile.getDataFromUUID(online.getUniqueId())));
                            if(p != online) {
                                String suffix = PlaceholderAPI.setPlaceholders(p, getTagData(p).getSuffix());
                                if(prof.isVanish()){
                                    suffix = " §c[已隐身]";
                                }
                                String priority = Method.getTagPriority(p, prof);
                                Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
                            }
                            if(PlayerProfile.getDataFromUUID(online.getUniqueId()) != null) {
                                String suffix = PlaceholderAPI.setPlaceholders(online, getTagData(online).getSuffix());
                                PlayerProfile profile = PlayerProfile.getDataFromUUID(online.getUniqueId());
                                if (profile.isVanish()) {
                                    suffix = " §c[已隐身]";
                                }
                                String priority = Method.getTagPriority(online, profile);
                                Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, suffix), TeamAction.CREATE, priority);
                            }
                        }
                    }

                    //nick check
                    if(((prof.isNicked() && Main.getGameManager() != null) || (prof.isNicked() && p.hasPermission("ncs.nick.staff"))) && !Main.getInstance().getConfig().getBoolean("disable-nick")){
                        if(!Main.getSQL().checkDataExists("player_data", "player_name", prof.getNickName())) {
                            Main.getNMS().changeName(p, prof.getNickName());
                        }else {
                            prof.setNicked(false);
                            prof.setNickSkin("");
                            prof.setNickName("");
                            prof.setNickPrefix("");
                            prof.saveData(false);
                            p.sendMessage("§c有玩家使用了此昵称所以已经将你的昵称还原！");
                            SystemAPI.getApi().refreshTag(p);
                        }
                    }else if(prof.isNicked() && !Main.getInstance().getConfig().getBoolean("disable-nick")) {
                        Main.getNMS().restoreName(p);
                    }

                    //join message
                    p.setDisplayName(PlaceholderAPI.setPlaceholders(p, "%profile_prefix%") + p.getName() + PlaceholderAPI.setPlaceholders(p, "%profile_suffix%"));
                    if (SettingConfig.cfg.getYml().getBoolean("Setting.JoinMessage.Enable")) {
                        if (!prof.isVanish()) {
                            String message = SettingConfig.cfg.getYml().getString("Setting.JoinMessage.Text")
                                    .replace("{displayname}", p.getDisplayName())
                                    .replace("{name}", p.getName());
                            for (Player online : Bukkit.getOnlinePlayers()) {
                                online.sendMessage(message);
                                online.playSound(p.getLocation(), Sound.valueOf(Main.getNMS().joinSound()), 3.0F, 1.3F);
                            }
                        }
                    }

                    //vanish check
                    if (prof.isVanish()) {
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            vanishPlayer(p, online, true);
                        }
                        PlayerProfile.vanishs.add(e.getPlayer().getUniqueId());
                    }
                    for (UUID uuid : PlayerProfile.vanishs) {
                        PlayerProfile profile = PlayerProfile.getDataFromUUID(uuid);
                        if (profile.isVanish()) {
                            vanishPlayer(Bukkit.getPlayer(uuid), p, true);
                        }
                    }
                    prof.checkStatus();
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
        if (SettingConfig.cfg.getYml().getBoolean("Setting.JoinMessage.Enable")) {
            e.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        if(prof == null){
            return;
        }
        SystemAPI.getApi().refreshTag(p);
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
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, Method.getTagPriority(p, prof));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, PlayerProfile.getDataFromUUID(online.getUniqueId())));
                //restore tag
                if(p != online) {
                    String suffix = PlaceholderAPI.setPlaceholders(p, getTagData(p).getSuffix());
                    if(prof.isVanish()){
                        suffix = " §c[已隐身]";
                    }
                    String priority = Method.getTagPriority(p, prof);
                    Main.getNMS().changeNameTag(online, p, PlaceholderAPI.setPlaceholders(p, getTagData(p).getPrefix()), suffix, TeamAction.CREATE, priority);
                }
                if(PlayerProfile.getDataFromUUID(online.getUniqueId()) != null) {
                    String suffix = PlaceholderAPI.setPlaceholders(online, getTagData(online).getSuffix());
                    PlayerProfile profile = PlayerProfile.getDataFromUUID(online.getUniqueId());
                    if (profile.isVanish()) {
                        suffix = " §c[已隐身]";
                    }
                    String priority = Method.getTagPriority(online, profile);
                    Main.getNMS().changeNameTag(p, online, PlaceholderAPI.setPlaceholders(online, getTagData(online).getPrefix()), PlaceholderAPI.setPlaceholders(online, suffix), TeamAction.CREATE, priority);
                }
            }
        }
        for (UUID uuid : PlayerProfile.vanishs) {
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
        if (TagConfig.cfg.getBoolean("enabled") && TagConfig.cfg.getYml().getStringList("enabled-world").contains(p.getWorld().getName())) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (p != online)
                    Main.getNMS().changeNameTag(online, p, "", "", TeamAction.DESTROY, Method.getTagPriority(p, profile));
                Main.getNMS().changeNameTag(p, online, "", "", TeamAction.DESTROY, Method.getTagPriority(online, PlayerProfile.getDataFromUUID(online.getUniqueId())));
            }
        }
        PlayerProfile.vanishs.remove(e.getPlayer().getUniqueId());
        if (SettingConfig.cfg.getYml().getBoolean("Setting.QuitMessage.Enable")) {
            if (!profile.isVanish()) {
                e.setQuitMessage(SettingConfig.cfg.getYml().getString("Setting.QuitMessage.Text")
                        .replace("{displayname}", p.getDisplayName())
                        .replace("{name}", p.getName()));
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.playSound(p.getLocation(), Sound.valueOf(Main.getNMS().quitSound()), 3.0F, 1.0F);
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
