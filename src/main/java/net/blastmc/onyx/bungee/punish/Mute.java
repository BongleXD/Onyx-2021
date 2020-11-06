package net.blastmc.onyx.bungee.punish;

public class Mute implements Punish{

    private String executorName;
    private String server;
    private long muteMillis;
    private long duration;
    private String muteID;
    private String reason;

    public Mute(String executorName, String server, long muteMillis, long duration, String muteID, String reason){
        this.executorName = executorName;
        this.server = server;
        this.muteMillis = muteMillis;
        this.duration = duration;
        this.muteID = muteID;
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
        return this.muteMillis;
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public String getPunishID() {
        return this.muteID;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

}
