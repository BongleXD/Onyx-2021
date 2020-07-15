package net.blastmc.onyx.bungee.api;

import net.blastmc.onyx.bungee.Main;
import net.blastmc.onyx.shared.util.SQLHelper;

public class MuteData {

    private static SQLHelper sql = Main.getSQL();

    public static void init(){
        sql.create("player_mutes",
                new SQLHelper.Value(SQLHelper.ValueType.ID, "id"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "uuid"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "ip"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "reason"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "muted_by"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "unmuted_by"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "muted_time"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "unmuted_time"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "appeals"),
                new SQLHelper.Value(SQLHelper.ValueType.STRING, "server"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "ipmute"),
                new SQLHelper.Value(SQLHelper.ValueType.INTEGER, "active"));
    }

}
