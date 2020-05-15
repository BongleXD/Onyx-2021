package cn.newcraft.system.bukkit.api;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.config.TagConfig;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Set;

public class TagData {

    private static int maxLength = 0;
    private String name;
    private String prefix;
    private String suffix;
    private String perm;
    private int priority;
    private static HashMap<String, TagData> dataMap = new HashMap<>();

    public static void setMaxLength(int length){
        maxLength = length;
    }

    public TagData(String name, String prefix, String suffix, String perm, int priority) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.perm = perm;
        this.priority = priority;
        dataMap.put(name, this);
    }

    public static void init(){
        if(TagConfig.cfg.getBoolean("enabled")){
            if(TagConfig.cfg.getBoolean("mysql")) {
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if (!Main.getSQL().checkDataExists("player_tag", "name", "default")) {
                            Main.getSQL().putFlag("player_tag", "name", "default");
                            Main.getSQL().putData("player_tag", "name", "default", "prefix", "%profile_prefix%");
                            Main.getSQL().putData("player_tag", "name", "default", "suffix", "%profile_suffix%");
                            Main.getSQL().putData("player_tag", "name", "default", "perm", "");
                            Main.getSQL().putData("player_tag", "name", "default", "priority", 1);
                        }
                        int i = 1;
                        while (true) {
                            Object[] array = Main.getSQL().getAllData("player_tag", i, 5);
                            if(array[0] == null){
                                break;
                            }
                            setMaxLength(Math.max(maxLength, String.valueOf((int) array[4]).length()));
                            new TagData((String) array[0], (String) array[1], (String) array[2], (String) array[3], (int) array[4]);
                            i++;
                        }
                    }
                }.runTaskAsynchronously(Main.getInstance());
            }else {
                for (String group : TagConfig.cfg.getYml().getConfigurationSection("group").getKeys(false)) {
                    if (dataMap.containsKey(group)) {
                        continue;
                    }
                    setMaxLength(Math.max(maxLength, String.valueOf(TagConfig.cfg.getInt("group." + group + ".priority")).length()));
                    new TagData(group, TagConfig.cfg.getString("group." + group + ".prefix"), TagConfig.cfg.getString("group." + group + ".suffix"), TagConfig.cfg.getString("group." + group + ".perm"), TagConfig.cfg.getInt("group." + group + ".priority"));
                }
            }
            dataMap.forEach((s, tagData) -> {
                System.out.println(s + " 已导入！");
            });
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getPerm(){
        return this.perm;
    }

    public static TagData getData(String name){
        return dataMap.getOrDefault(name, null);
    }

    public String getName() {
        return name;
    }

    public String getPriority() {
        int length = String.valueOf(priority).length();
        StringBuilder sb = new StringBuilder();
        if(length < maxLength){
            for(int j = 0; j < maxLength - length; j++){
                sb.append("0");
            }
        }
        sb.append(priority);
        return sb.toString();
    }

    public int getIntegerPriority() {
        return priority;
    }

    public static Set<String> getTagGroups(){
        return dataMap.keySet();
    }

}
