package net.blastmc.onyx.api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathHelper {

    public final static double PI = 3.14159265358979323846;

    public static final double E = 2.7182818284590452354;

    public static double abs(double a){
        if(a < 0){
            a *= -1;
        }
        return a;
    }

    public static double sin(double a) {
        return Math.sin(a);
    }

    public static double cos(double a) {
        return Math.cos(a);
    }

    public static double tan(double a) {
        return Math.tan(a);
    }

    public static double cot(double a) {
        return tan(toRadians(a));
    }

    public static double toRadians(double a) {
        return a * PI / 180;
    }


    public static double add(double x, double y){
        return new BigDecimal(Double.toString(x))
                .add(new BigDecimal(Double.toString(y)))
                .doubleValue();
    }

    public static double sub(double x, double y){
        return new BigDecimal(Double.toString(x))
                .subtract(new BigDecimal(Double.toString(y)))
                .doubleValue();
    }

    public static double div(double x, double y, int scale){
        return new BigDecimal(Double.toString(x))
                .divide(new BigDecimal(Double.toString(y)), scale, RoundingMode.UNNECESSARY)
                .doubleValue();
    }

    public static double div(double x, double y, int scale, RoundingMode mode){
        return new BigDecimal(Double.toString(x))
                .divide(new BigDecimal(Double.toString(y)), scale, mode)
                .doubleValue();
    }

    public static double mul(double x, double y){
        return new BigDecimal(Double.toString(x))
                .multiply(new BigDecimal(Double.toString(y)))
                .doubleValue();
    }

}
