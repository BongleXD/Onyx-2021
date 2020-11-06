package net.blastmc.onyx.bungee.punish;

public class Ban implements Punish{

    private String executorName;
    private String server;
    private long banMillis;
    private long duration;
    private String banID;
    private String reason;

    public Ban(String executorName, String server, long banMillis, long duration, String banID, String reason){
        this.executorName = executorName;
        this.server = server;
        this.banMillis = banMillis;
        this.duration = duration;
        this.banID = banID;
        this.reason = reason;
    }

    @Override
    public String getExecutorName() {
        return this.executorName;
    }

    @Override
    public String getServer() {
        return this.server;
    }

    @Override
    public long getPunishTimeMillis() {
        return this.banMillis;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public String getPunishID() {
        return this.banID;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

}
