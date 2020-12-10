package net.blastmc.onyx.bungee.punish;

public enum PunishType {

    BAN,
    WARN,
    MUTE,
    KICK;

    public String getPrefix(){
        switch (this){
            case BAN:
                return "GG";
            case MUTE:
                return "MU";
            default:
                return null;
        }
    }

    public String getSqlPrefix(){
        switch (this){
            case MUTE:
                return "mute";
            case KICK:
                return "kick";
            case BAN:
                return "ban";
            case WARN:
                return "warn";
            default:
                return null;
        }
    }

}
