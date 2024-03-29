package net.blastmc.onyx.bukkit.level;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.api.utils.SQLHelper;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
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
            claimList = sql.getColumnData("claim_data", "pid", pid, 2);
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
        return dataMap.getOrDefault(Onyx.getAPI().getOfflinePID(uuid), null);
    }

    public void saveData(boolean destroy){
        for (int level : newClaim){
            sql.insertData("claim_data",
                    new SQLHelper.SqlValue("pid", pid),
                    new SQLHelper.SqlValue("level", level));
        }
        if(destroy){
            dataMap.remove(pid);
        }
    }

    public static void init(){
        sql.create("claim_data",
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "pid"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "level"));
    }

}
