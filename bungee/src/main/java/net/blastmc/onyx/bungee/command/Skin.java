package net.blastmc.onyx.bungee.command;

import net.blastmc.onyx.bungee.api.SkinAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

public class Skin extends Command {

    private HashMap<UUID, Long> coolDownMap = new HashMap<>();

    public Skin() {
        super("skin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)){
            sender.sendMessage("§c该指令不能在控制台执行！");
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) sender;
        if(!p.getServer().getInfo().getName().toLowerCase().contains("lobby")){
            p.sendMessage("§c请在大厅使用此指令！");
            return;
        }
        if(coolDownMap.containsKey(p.getUniqueId())) {
            if (coolDownMap.get(p.getUniqueId()) > System.currentTimeMillis()) {
                double coolDown = ((double) coolDownMap.get(p.getUniqueId()) - (double) System.currentTimeMillis()) / 1000;
                if (coolDown == 0.0) {
                    coolDown = 0.1;
                }
                p.sendMessage("§c指令冷却中！ 请 §e" + roundDouble(coolDown, 1) + " §c秒后再试！");
                return;
            }
        }
        p.sendMessage("§a已将你的皮肤更新至最新！请重新加入服务器");
        SkinAPI.getApi().reloadPlayer(p);
        coolDownMap.put(p.getUniqueId(), System.currentTimeMillis() + (1000 * 60 * 5));
    }

    public static double roundDouble(double data, int scale){
        return new BigDecimal(data).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
