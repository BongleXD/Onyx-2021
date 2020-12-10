package net.blastmc.onyx.bungee.punish;

public class Warn implements Punish{

    private String executorName;
    private String server;
    private long warnMillis;
    private String reason;

    public Warn(String executorName, String server, long warnMillis, String reason){
        this.executorName = executorName;
        this.server = server;
        this.warnMillis = warnMillis;
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
        return this.warnMillis;
    }

    @Override
    public long getDuration() {
        return -1;
    }

    @Override
    public String getPunishID() {
        return null;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

}
