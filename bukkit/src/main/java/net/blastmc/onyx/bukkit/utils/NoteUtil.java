package net.blastmc.onyx.bukkit.utils;

import java.math.BigDecimal;

public enum NoteUtil {

    F3_SHARP(0.5F),
    G3(BigDecimal.valueOf(Math.pow(2, -11.0 / 12.0)).floatValue()),
    G3_SHARP(BigDecimal.valueOf(Math.pow(2, -10.0 / 12.0)).floatValue()),
    A3(BigDecimal.valueOf(Math.pow(2, -9.0 / 12.0)).floatValue()),
    A3_SHARP(BigDecimal.valueOf(Math.pow(2, -8.0 / 12.0)).floatValue()),
    B3(BigDecimal.valueOf(Math.pow(2, -7.0 / 12.0)).floatValue()),
    C4(BigDecimal.valueOf(Math.pow(2, -6.0 / 12.0)).floatValue()),
    C4_SHARP(BigDecimal.valueOf(Math.pow(2, -5.0 / 12.0)).floatValue()),
    D4(BigDecimal.valueOf(Math.pow(2, -4.0 / 12.0)).floatValue()),
    D4_SHARP(BigDecimal.valueOf(Math.pow(2, -3.0 / 12.0)).floatValue()),
    E4(BigDecimal.valueOf(Math.pow(2, -2.0 / 12.0)).floatValue()),
    F4(BigDecimal.valueOf(Math.pow(2, -1.0 / 12.0)).floatValue()),
    F4_SHARP(1F),
    G4(BigDecimal.valueOf(Math.pow(2, 1.0 / 12.0)).floatValue()),
    G4_SHARP(BigDecimal.valueOf(Math.pow(2, 2.0 / 12.0)).floatValue()),
    A4(BigDecimal.valueOf(Math.pow(2, 3.0 / 12.0)).floatValue()),
    A4_SHARP(BigDecimal.valueOf(Math.pow(2, 4.0 / 12.0)).floatValue()),
    B4(BigDecimal.valueOf(Math.pow(2, 5.0 / 12.0)).floatValue()),
    C5(BigDecimal.valueOf(Math.pow(2, 6.0 / 12.0)).floatValue()),
    C5_SHARP(BigDecimal.valueOf(Math.pow(2, 7.0 / 12.0)).floatValue()),
    D5(BigDecimal.valueOf(Math.pow(2, 8.0 / 12.0)).floatValue()),
    D5_SHARP(BigDecimal.valueOf(Math.pow(2, 9.0 / 12.0)).floatValue()),
    E5(BigDecimal.valueOf(Math.pow(2, 10.0 / 12.0)).floatValue()),
    F5(BigDecimal.valueOf(Math.pow(2, 11.0 / 12.0)).floatValue()),
    F5_SHARP(2F);

    private float pitch;

    NoteUtil(float pitch){
        this.pitch = pitch;
    }

    public float getPitch() {
        return pitch;
    }

    public NoteUtil getNextNote() {
        try {
            return values()[(this.ordinal() + 1)];
        }catch (ArrayIndexOutOfBoundsException ex){
            return null;
        }
    }

    public NoteUtil getNextNote(int i) {
        try {
            return values()[(this.ordinal() + i)];
        }catch (ArrayIndexOutOfBoundsException ex){
            return null;
        }
    }

}
