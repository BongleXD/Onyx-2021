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
        return this.name().toLowerCase();
    }

}
