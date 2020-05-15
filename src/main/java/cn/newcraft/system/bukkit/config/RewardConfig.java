package cn.newcraft.system.bukkit.config;

public class RewardConfig extends ConfigManager{

    public static RewardConfig cfg;

    public RewardConfig() {
        super("rewards", "plugins/NewCraftSystem");
    }

    public static void init(){
        RewardConfig.cfg = new RewardConfig();
        cfg.getYml().options().copyDefaults(true);
        cfg.getYml().addDefault("rewards.1.command", new String[]{"/test"});
        cfg.getYml().addDefault("rewards.1.lore", new String[]{"&7这是个奖励 awa"});
        cfg.getYml().addDefault("rewards.2.command", new String[]{"/test"});
        cfg.getYml().addDefault("rewards.2.lore", new String[]{"&7这是个奖励 awa"});
        cfg.getYml().addDefault("rewards.3.command", new String[]{"/test"});
        cfg.getYml().addDefault("rewards.3.lore", new String[]{"&7这是个奖励 awa"});
        cfg.getYml().addDefault("rewards.4.command", new String[]{"/test"});
        cfg.getYml().addDefault("rewards.4.lore", new String[]{"&7这是个奖励 awa"});
        cfg.getYml().addDefault("rewards.5.command", new String[]{"/test"});
        cfg.getYml().addDefault("rewards.5.lore", new String[]{"&7这是个奖励 awa"});
        cfg.getYml().addDefault("rewards.6.command", new String[]{"/test"});
        cfg.getYml().addDefault("rewards.6.lore", new String[]{"&7这是个奖励 awa"});
        cfg.getYml().addDefault("rewards.7.command", new String[]{"/test"});
        cfg.getYml().addDefault("rewards.7.lore", new String[]{"&7这是个奖励 awa"});
        cfg.getYml().addDefault("rewards.8.command", new String[]{"/test"});
        cfg.getYml().addDefault("rewards.8.lore", new String[]{"&7这是个奖励 awa"});
        cfg.getYml().addDefault("rewards.9.command", new String[]{"/test"});
        cfg.getYml().addDefault("rewards.9.lore", new String[]{"&7这是个奖励 awa"});
        cfg.save();
    }

}
