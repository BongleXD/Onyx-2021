package net.blastmc.onyx.bungee.util;

import java.util.Random;

import static net.blastmc.onyx.shared.util.Method.toTrisection;

public class BungeeMethod {

    public static String getRandomString(int length) {
        String str = "123456789ABCDEFGHJKLMNPQRSTUVWXY";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String longToTime(long time){
        StringBuilder sb = new StringBuilder();
        int mins = (int) (time / 1000) / 60;
        int hours = 0;
        int days = 0;
        if(mins >= 60){
            hours = mins / 60;
            mins = mins % 60;
        }
        if(hours >= 24){
            days = hours / 24;
            hours = hours % 24;
        }
        if (days > 0) {
            sb.append(toTrisection(days)).append(" 天").append(hours > 0 ? ", " + hours + " 小时" : "").append(mins > 0 ? ", " + mins + " 分钟" : "");
        } else if (hours > 0) {
            sb.append(hours).append(" 小时").append(mins > 0 ? ", " + mins + " 分钟" : "");
        } else if (mins > 0) {
            sb.append(mins).append(" 分钟");
        }
        if(!sb.toString().isEmpty()){
            return sb.toString();
        }
        return "不到一分钟";
    }

}
