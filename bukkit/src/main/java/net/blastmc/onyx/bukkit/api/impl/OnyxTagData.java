package net.blastmc.onyx.bukkit.api.impl;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.bukkit.TagData;
import net.blastmc.onyx.api.utils.Log;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.config.TagConfig;
import net.blastmc.onyx.api.utils.SQLHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class OnyxTagData implements TagData {

    private static int maxLength = 0;
    private String name;
    private String prefix;
    private String suffix;
    private String perm;
    private int priority;
    private static HashMap<String, OnyxTagData> dataMap = new HashMap<>();

    public static void setMaxLength(int length){
        maxLength = length;
    }

    public OnyxTagData(String name, String prefix, String suffix, String perm, int priority) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.perm = perm;
        this.priority = priority;
        dataMap.put(name, this);
    }

    private static void create() {
        Main.getSQL().create("player_tag",
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "name"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "prefix"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "suffix"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "perm"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "priority"));
    }

    public static void init(){
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            if(TagConfig.ENABLED){
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Onyx.getAPI().removeTag(online.getUniqueId());
                }
                dataMap.clear();
                if(TagConfig.MYSQL_ENABLED) {
                    create();
                    if (!Main.getSQL().checkDataExists("player_tag", "name", "default")) {
                        Main.getSQL().putData("player_tag", "name", "default",
                                new SQLHelper.SqlValue("prefix", "%profile_prefix%"),
                                new SQLHelper.SqlValue("suffix", "%profile_suffix%"),
                                new SQLHelper.SqlValue("perm", ""),
                                new SQLHelper.SqlValue("priority", 1));
                    }
                    int i = 1;
                    while (true) {
                        List list = Main.getSQL().getRowData("player_tag", i);
                        if(list == null || list.isEmpty()){
                            break;
                        }
                        setMaxLength(Math.max(maxLength, String.valueOf((int) list.get(4)).length()));
                        new OnyxTagData((String) list.get(0), (String) list.get(1), (String) list.get(2), (String) list.get(3), (int) list.get(4));
                        i++;
                    }
                }else {
                    for (String group : TagConfig.config.getYml().getConfigurationSection("group").getKeys(false)) {
                        if (dataMap.containsKey(group)) {
                            continue;
                        }
                        setMaxLength(Math.max(maxLength, String.valueOf(TagConfig.config.getInt("group." + group + ".priority")).length()));
                        new OnyxTagData(group, TagConfig.config.getString("group." + group + ".prefix"), TagConfig.config.getString("group." + group + ".suffix"), TagConfig.config.getString("group." + group + ".perm"), TagConfig.config.getInt("group." + group + ".priority"));
                    }
                }
                dataMap.forEach((s, tagData) -> {
                    Log.getLogger().sendLog(s + " 已导入！");
                });
                Onyx.getAPI().refreshAllTag();
            }
        });
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

    public static OnyxTagData getData(String name){
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
