package net.blastmc.onyx.bukkit.utils.placeholders;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.server.ServerType;
import net.blastmc.onyx.bukkit.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.blastmc.onyx.api.utils.Method;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ProfilePlaceholders {

    public ProfilePlaceholders() {
        registerPlaceHolders();
    }

    private void registerPlaceHolders() {
        PlaceholderAPI.registerPlaceholderHook("profile", new PlaceholderHook() {
            @Override
            public String onRequest(OfflinePlayer p, String s) {
                if (p != null && p.isOnline()) {
                    return onPlaceholderRequest(p.getPlayer(), s);
                }
                return null;
            }

            @Override
            public String onPlaceholderRequest(Player p, String s) {
                try {
                    PlayerProfile profile = Onyx.getPlayerProfile(p.getUniqueId());
                    if (s.equals("rank")) {
                        return profile.getRank().getDisplayName();
                    }
                    if (s.equals("rank_time")) {
                        String rank = PlaceholderAPI.setPlaceholders(p, "%luckperms_primary_group_name%");
                        String time = PlaceholderAPI.setPlaceholders(p, "%luckperms_group_expiry_time_" + rank + "%")
                                .replace("d", "天")
                                .replace("h", "时")
                                .replace("m", "分")
                                .replace("s", "秒");
                        return time.isEmpty() ? "永久" : time;
                    }
                    if (s.equals("color")) {
                        if((Main.getType() != ServerType.GAME && Main.getType() != ServerType.ENDLESS_GAME) && !p.hasPermission("onyx.nick.staff")){
                            return profile.getRank().getColor();
                        }
                        if(profile.isNicked() && !Main.getInstance().getConfig().getBoolean("disable-nick") && !profile.getNickPrefix().equals("self")){
                            return profile.getNickPrefix().substring(0, 2);
                        }else{
                            return profile.getRank().getColor();
                        }
                    }
                    if (s.equals("prefix")) {
                        if((Main.getType() != ServerType.GAME && Main.getType() != ServerType.ENDLESS_GAME) && !p.hasPermission("onyx.nick.staff")){
                            return Main.getVault().getPlayerPrefix(p);
                        }
                        if(profile.isNicked() && !Main.getInstance().getConfig().getBoolean("disable-nick")){
                            if(profile.getNickPrefix().equals("self")){
                                return Main.getVault().getPlayerPrefix(p);
                            }else{
                                return profile.getNickPrefix();
                            }
                        }else{
                            return Main.getVault().getPlayerPrefix(p);
                        }
                    }
                    if (s.equals("suffix")) {
                        if((Main.getType() != ServerType.GAME && Main.getType() != ServerType.ENDLESS_GAME) && !p.hasPermission("onyx.nick.staff")){
                            return Main.getVault().getPlayerSuffix(p);
                        }
                        if(profile.isNicked() && !Main.getInstance().getConfig().getBoolean("disable-nick")){
                            return "";
                        }else{
                            return Main.getVault().getPlayerSuffix(p);
                        }
                    }
                    if (s.equals("level")) {
                        return Method.toTrisection(profile.getLevel());
                    }
                    if (s.equals("xp")) {
                        return Method.toTrisection(profile.getXp());
                    }
                    if (s.equals("progress")) {
                        return profile.getProgressBar();
                    }
                    if (s.equals("pid")) {
                        return profile.getSecondPasswd().isEmpty() ? "BM-XXXX-XXXX-XXXX" : profile.getPID();
                    }
                    if (s.equals("xpneed")) {
                        return Method.toTrisection(profile.getXpToLevelUp() - profile.getXp());
                    }
                }catch (Exception ignored){ }
                return "";
            }
        });
    }
}
