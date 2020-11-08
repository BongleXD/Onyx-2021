package net.blastmc.onyx.shared.util;

import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;

public class Method {

    public static String getPercent(double x, double y){
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("0.0");
        return df.format(x / y * 100).replace(".0", "") + "%";
    }

    public static String transColor(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String toTrisection(double d) {
        if (d == 0) {
            return "0";
        }
        DecimalFormat df = new DecimalFormat("#,###.00");
        String result = df.format(d).replace(".00", "");
        return result.endsWith(".0") ? result.replace(".0", "") : result.contains(".") && result.endsWith("0") ? result.substring(0, result.length() - 1) : result;
    }

    public static String toKilo(int number){
        int i = number / 100;
        if(number < 1000){
            return String.valueOf(number);
        }
        else if(i % 10 == 0){
            return number / 1000 + "k";
        }
        else if(number % 1000 == 0){
            return number / 1000 + "k";
        }
        else if(number > 1000){
            double d = (double) number / (double) 100;
            d = Math.floor(d);
            return d / 10 + "k";
        }
        else{
            return String.valueOf(number);
        }
    }

    public static String toRoman(int number) {
        StringBuilder rNumber = new StringBuilder();
        int[] aArray = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] rArray = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X",
                "IX", "V", "IV", "I"};
        if (number < 1 || number > 3999) {
            rNumber = new StringBuilder();
        } else {
            for (int i = 0; i < aArray.length; i++) {
                while (number >= aArray[i]) {
                    rNumber.append(rArray[i]);
                    number -= aArray[i];
                }
            }
        }
        return rNumber.toString();
    }

    public static String getProgressBar(int xp, int xpToLevelUp, int length, String symbol, String unlock, String lock) {
        StringBuilder sb = new StringBuilder();
        if (xpToLevelUp == 0) {
            return "";
        }
        int max = xp * length / xpToLevelUp;
        for (int i = 0; i < length; i++) {
            if (i < max) {
                sb.append(unlock).append(symbol);
            } else {
                sb.append(lock).append(symbol);
            }
        }
        return sb.toString();
    }


    public static double roundDouble(double data, int scale){
        return new BigDecimal(data).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static HashMap readUrl(String urlString) {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
            HashMap map = new HashMap<>();
            map = new Gson().fromJson(buffer.toString(), map.getClass());
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
