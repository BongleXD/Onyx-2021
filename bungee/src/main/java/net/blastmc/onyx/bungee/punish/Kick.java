package net.blastmc.onyx.bungee.punish;

public class Kick implements Punish{
    private String executorName;
    private String server;
    private long kickMillis;
    private String reason;

    public Kick(String executorName, String server, long kickMillis, String reason){
        this.executorName = executorName;
        this.server = server;
        this.kickMillis = kickMillis;
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
        return this.kickMillis;
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
