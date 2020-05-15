package cn.newcraft.system.bukkit.util.placeholders;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.util.Method;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
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
                    PlayerProfile profile = PlayerProfile.getDataFromUUID(p.getUniqueId());
                    if (s.equals("rank")) {
                        return profile.getRank().getDisplayName();
                    }
                    if (s.equals("color")) {
                        if(Main.getGameManager() == null && !p.hasPermission("ncs.nick.staff")){
                            return profile.getRank().getColor();
                        }
                        if(profile.isNicked() && !Main.getInstance().getConfig().getBoolean("disable-nick") && !profile.getNickPrefix().equals("self")){
                            return profile.getNickPrefix().substring(0, 2);
                        }else{
                            return profile.getRank().getColor();
                        }
                    }
                    if (s.equals("prefix")) {
                        if(Main.getGameManager() == null && !p.hasPermission("ncs.nick.staff")){
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
                        if(Main.getGameManager() == null && !p.hasPermission("ncs.nick.staff")){
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
                        return profile.getSecondPasswd().isEmpty() ? "NC-XXXX-XXXX-XXXX" : profile.getPID();
                    }
                    if (s.equals("xpneed")) {
                        return Method.toTrisection(profile.getXpToLevelUp() - profile.getXp());
                    }
                    if (s.equals("coin")) {
                        return Method.toTrisection(Integer.parseInt(PlaceholderAPI.setPlaceholders(p, "%gemseconomy_balance_default%")));
                    }
                    if (s.equals("points")) {
                        return Method.toTrisection(Integer.parseInt(PlaceholderAPI.setPlaceholders(p, "%playerpoints_points%")));
                    }
                }catch (Exception ignored){ }
                return "";
            }
        });
    }
}
