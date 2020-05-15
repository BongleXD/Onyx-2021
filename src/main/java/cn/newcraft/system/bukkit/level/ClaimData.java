package cn.newcraft.system.bukkit.level;

import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.shared.util.SQLHelper;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClaimData {

    private static SQLHelper sql = Main.getSQL();
    private String pid;
    private List<Integer> claimList = Lists.newArrayList();
    private List<Integer> newClaim = Lists.newArrayList();
    private static HashMap<String, ClaimData> dataMap = new HashMap<>();

    public ClaimData(String pid){
        this.pid = pid;
        putData();
        dataMap.put(pid, this);
    }

    private void putData(){
        boolean b = sql.checkDataExists("claim_data", "pid", pid);
        if(b){
            claimList = sql.getAllData("claim_data", "pid", pid, 3);
        }
    }

    public List<Integer> getClaimList() {
        return claimList;
    }

    public void addClaim(int level){
        this.claimList.add(level);
        this.newClaim.add(level);
    }

    public static ClaimData getDataFromUUID(UUID uuid){
        String pid = Objects.requireNonNull(PlayerData.getDataFromUUID(uuid)).getPID();
        return dataMap.getOrDefault(pid, null);
    }

    public void saveData(boolean destroy){
        newClaim.forEach(level -> {
            sql.insertData("claim_data", new Object[]{"pid", "level"}, new Object[]{pid, level});
        });
        if(destroy){
            dataMap.remove(pid);
        }
    }

    public static void init(){
        sql.create("claim_data");
        sql.addStringColumn("claim_data", "pid");
        sql.addIntegerColumn("claim_data", "level");
    }

}
