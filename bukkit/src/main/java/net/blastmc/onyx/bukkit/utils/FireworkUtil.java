package net.blastmc.onyx.bukkit.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FireworkUtil {

    private List<Color> colors = new ArrayList<>();
    private List<FireworkEffect.Type> types = new ArrayList<>();

    public FireworkUtil(){
        init();
    }

    public void init(){
        colors.add(Color.WHITE);
        colors.add(Color.PURPLE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.AQUA);
        colors.add(Color.BLUE);
        colors.add(Color.FUCHSIA);
        colors.add(Color.GRAY);
        colors.add(Color.LIME);
        colors.add(Color.MAROON);
        colors.add(Color.YELLOW);
        colors.add(Color.SILVER);
        colors.add(Color.TEAL);
        colors.add(Color.ORANGE);
        colors.add(Color.OLIVE);
        colors.add(Color.NAVY);
        colors.add(Color.BLACK);
        types.add(FireworkEffect.Type.BURST);
        types.add(FireworkEffect.Type.BALL);
        types.add(FireworkEffect.Type.BALL_LARGE);
        types.add(FireworkEffect.Type.CREEPER);
        types.add(FireworkEffect.Type.STAR);
    }

    public FireworkEffect.Type getRandomType(){
        int size = types.size();
        Random r = new Random();
        return types.get(r.nextInt(size));
    }

    public Color getRandomColor(){
        int size = colors.size();
        Random r = new Random();
        return colors.get(r.nextInt(size));
    }

    public void launch(Location loc){
        Firework fw = loc.getWorld().spawn(loc, org.bukkit.entity.Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.setPower(1);
        fm.addEffects(FireworkEffect.builder().with(getRandomType()).withColor(getRandomColor()).build());
        fw.setFireworkMeta(fm);
    }

}
