package net.blastmc.onyx.bukkit.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkinConfig extends ConfigManager{

    public static SkinConfig config;
    @Config(path = "skin")
    public static List<String> SKINS = new ArrayList<>(Collections.singletonList("Hello_Han"));

    public SkinConfig() {
        super("skins", "plugins/Onyx");
        config = this;
    }

}
