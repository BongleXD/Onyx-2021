package cn.newcraft.system.bungee.config;

public class SkinConfig extends ConfigManager{

    public static SkinConfig cfg;

    public SkinConfig() {
        super("skin");
    }

    public static void init(){
        SkinConfig.cfg = new SkinConfig();
        boolean b = cfg.getYml().contains("Hello_Han");
        if(!b){
            cfg.getYml().set("Hello_Han.value", "ewogICJ0aW1lc3RhbXAiIDogMTU4ODMxMjc0NDU3NSwKICAicHJvZmlsZUlkIiA6ICI4OGQ3MGViMzI1NmM0OWNlYTVlNzc0NzZhZmVmNGZjMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJIZWxsb19IYW4iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTU1MzIwNDEzMTU3MTE5MDMwMDJjYWUxYzE4MzZlN2EzMjg4MzdlOTRmMzhiYWE3NzE3MDkwN2IzZjFmYWYyZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9");
            cfg.getYml().set("Hello_Han.signature", "xBk2RL4pE9WpXRrtlFcdp0jhdcUH6VqQfJqO5se/qQTmOLQnxT7czbu31DlL1x5cneDXLAhNHUskP2KRAy6whTEdBzcoMks/mcH3v37DVAq2RnDGw088ubkFljGoAJdZH8F6xet1kwi0FCcpIk9PtsfOUphVhyPmFzOgdJt1qCyyAzAcXbLBKSQoEb/fvssHbZdWQJDeveEXhBgRpIe1dv9014PgQHV77MLVLotaVLmYvPi0YToY1jzZd7Y5LeSsgRpwQmeVLHdruV5d1tygHieooK6boDaqBDVeQJjrBz39qOCm2A4nD0hoHG+JO4dNy6elVReVABZjzmSytGpAAhLIBE4J+gNw8pg0T8lPAqgcE7DNvU/54Peo1P8vKYaeJsMhyaSdY5qfOdYXnYZvr/lZV0gg6w97fThrFyubqkKh7ORjYikQMRJ0Ty8aYrlTsygzQOdfjdULKtAmZrKdohDPVadCWk2W1JSONhbXKE40/YH+MBVnEE0SDykkIlbdvHo8POSiotvEfjwo9maBkfaJSfabh9j5lUfTXxiQ2bIu4XVaq1MuP4BqVGOXy6VYhM0KCSDbJHsgkX2T8TORFeuy6nph1qkBHlCMYlxI+xjMK5IbQSH1kbFrY1/13ZwBuhpBnqVPW7dboZpZGYsP3dPGVI6cuJsyzslvbd4OIuM=");
        }
        cfg.save();
    }

}
