package net.blastmc.onyx.bukkit.config;

import com.google.common.collect.Lists;
import net.blastmc.onyx.api.bukkit.Animation;
import net.blastmc.onyx.api.util.Log;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.api.bukkit.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class HologramConfig extends ConfigManager {

    public static HologramConfig config;
    public static HashMap<String, Hologram> holoMap;

    public HologramConfig() {
        super("hologram");
        config = this;
        load();
        this.getYml().options().copyDefaults(true);
        if(this.getYml().getConfigurationSection("animation") == null
                || this.getYml().getConfigurationSection("animation").getKeys(false) == null
                || this.getYml().getConfigurationSection("animation").getKeys(false).isEmpty()) {
            this.getYml().addDefault("animation.example", Arrays.asList("&aexample:0", "&cexample:0"));
        }
        save();
    }

    @Override
    public void save() {
        for(Map.Entry<String, Hologram> entry : holoMap.entrySet()){
            String name = entry.getKey();
            Hologram holo = entry.getValue();
            Location loc = holo.getLocation();
            config.getYml().set("hologram." + name + ".world", loc.getWorld().getName());
            config.getYml().set("hologram." + name + ".x", loc.getX());
            config.getYml().set("hologram." + name + ".y", loc.getY());
            config.getYml().set("hologram." + name + ".z", loc.getZ());
            config.getYml().set("hologram." + name + ".offset", holo.getOffset());
            config.getYml().set("hologram." + name + ".lines", holo.getLines());
        }
        super.save();
    }

    public void load(){
        holoMap = new HashMap<>();
        try {
            for (String path : config.getYml().getConfigurationSection("hologram").getKeys(false)) {
                Hologram holo = Main.getNMS().newInstance(new Location(
                                Bukkit.getWorld(config.getString("hologram." + path + ".world")),
                                config.getYml().getDouble("hologram." + path + ".x"),
                                config.getYml().getDouble("hologram." + path + ".y"),
                                config.getYml().getDouble("hologram." + path + ".z")),
                        config.getYml().getStringList("hologram." + path + ".lines"));
                holoMap.put(path, holo);
                checkAnim(holo.offset(config.getYml().getDouble("hologram." + path + ".offset")))
                        .showTo(Bukkit.getOnlinePlayers()).show();
            }
        }catch (Exception ignored){}
    }

    public Hologram checkAnim(Hologram holo){
        List<String> lines = holo.getLines();
        for(int i = 0; i < lines.size(); i++){
            String line = lines.get(i);
            if(line.startsWith("{anim:") && line.endsWith("}")){
                line = line.substring(6, line.length() - 1);
                List<Animation.Value> values = Lists.newArrayList();
                try {
                    for (String str : config.getYml().getStringList("animation." + line)) {
                        String[] args = str.split(":");
                        values.add(new Animation.Value(args[0], Integer.parseInt(args[1])));
                    }
                    holo = holo.animation(i, new Animation(values));
                }catch (NullPointerException ex){
                    Log.getLogger().sendError("§b" + line + " §c全息图动画不存在！");
                }catch (NumberFormatException ex){
                    Log.getLogger().sendError("§b" + line + " §c全息图动画的秒数不为数字！");
                }
            }
        }
        return holo;
    }

    @Override
    public void reload(){
        Iterator<Hologram> it = holoMap.values().iterator();
        while (it.hasNext()){
            Hologram holo = it.next();
            holo.removeTo(Bukkit.getOnlinePlayers());
            it.remove();
        }
        super.reload();
        load();
    }

}
