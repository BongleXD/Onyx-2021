package net.blastmc.onyx.bukkit.utils.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.blastmc.onyx.api.utils.Method;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FormatPlaceholders {

    public FormatPlaceholders() {
        registerPlaceHolders();
    }

    private void registerPlaceHolders() {
        PlaceholderAPI.registerPlaceholderHook("format", new PlaceholderHook() {
            @Override
            public String onRequest(OfflinePlayer p, String s) {
                if (p != null && p.isOnline()) {
                    return onPlaceholderRequest(p.getPlayer(), s);
                }
                return null;
            }

            @Override
            public String onPlaceholderRequest(Player p, String s) {
                if(s.startsWith("toTrisection_")){
                    s = PlaceholderAPI.setPlaceholders(p, s.replace("/", "%"));
                    try {
                        double data = Double.parseDouble(s.replace("toTrisection_", ""));
                        return Method.toTrisection(data);
                    }catch (NumberFormatException ex){
                        return "§c未知";
                    }
                }
                if(s.startsWith("translateLuckPerms_")){
                    s = PlaceholderAPI.setPlaceholders(p, s.replace("/", "%"));
                    return s.replace("translateLuckPerms_", "")
                            .replace("d", "天")
                            .replace("h", "时")
                            .replace("m", "分")
                            .replace("s", "秒");
                }
                if(s.startsWith("toSuffix_")){
                    s = PlaceholderAPI.setPlaceholders(p, s.replace("/", "%"));
                    try {
                        double data = Double.parseDouble(s.replace("toKilo_", ""));
                        return Method.toSuffix((int) data);
                    }catch (NumberFormatException ex){
                        return "§c未知";
                    }
                }
                return null;
            }
        });
    }
}
