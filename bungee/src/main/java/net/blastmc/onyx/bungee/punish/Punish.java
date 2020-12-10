package net.blastmc.onyx.bungee.punish;

public interface Punish {

    String getExecutorName();

    String getServer();

    long getPunishTimeMillis();

    long getDuration();

    String getPunishID();

    String getReason();

}
