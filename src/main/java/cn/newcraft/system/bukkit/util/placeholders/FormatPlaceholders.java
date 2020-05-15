package cn.newcraft.system.bukkit.util.placeholders;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.util.Method;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
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
                    s = PlaceholderAPI.setPlaceholders(p, s.replace("<", "%").replace(">", "%"));
                    try {
                        double data = Double.parseDouble(s.replace("toTrisection_", ""));
                        return Method.toTrisection(data);
                    }catch (NumberFormatException ex){
                        return "§c未知";
                    }
                }
                if(s.startsWith("toKilo_")){
                    s = PlaceholderAPI.setPlaceholders(p, s.replace("<", "%").replace(">", "%"));
                    try {
                        double data = Double.parseDouble(s.replace("toKilo_", ""));
                        return Method.toKilo((int) data);
                    }catch (NumberFormatException ex){
                        return "§c未知";
                    }
                }
                return null;
            }
        });
    }
}
