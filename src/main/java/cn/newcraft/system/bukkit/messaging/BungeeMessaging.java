package cn.newcraft.system.bukkit.messaging;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.util.Method;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class  BungeeMessaging implements PluginMessageListener {

    public static HashMap<Player, String> map = new HashMap<>();

    public BungeeMessaging(){
        Bukkit.getMessenger().registerOutgoingPluginChannel(Main.getInstance(), "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(Main.getInstance(), "BungeeCord", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] b) {
        if (!channel.equals("BungeeCord")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(b);
        String subchannel = in.readUTF();
        if (subchannel.equalsIgnoreCase("GetServer")) {
            String serverName = in.readUTF();
            p.sendMessage("§eServer: " + serverName);
        }
        if (subchannel.equalsIgnoreCase("IP")) {
            String ip = in.readUTF();
            p.sendMessage("§eIP Address: " + ip);
            new BukkitRunnable() {
                @Override
                public void run() { p.sendMessage("§eCurrent Network: " + Method.readUrl("http://api.ipstack.com/" + ip + "?access_key=4e41e5c33c54bea828cc996b043dc463").get("country_code"));
                }
            }.runTaskAsynchronously(Main.getInstance());
        }
        if (subchannel.equalsIgnoreCase("UUIDOther")) {
            String playerName = in.readUTF();
            String uuid = in.readUTF();
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for(char c : uuid.toCharArray()){
                sb.append(c);
                if(i == 8 || i == 12 || i == 16 || i ==20){
                    sb.append("-");
                }
                i++;
            }
            map.put(p, sb.toString());
            if(playerName.isEmpty()){
                p.sendMessage("玩家不在线！");
                return;
            }
            p.sendMessage("§eUser ID: " + playerName);
            p.sendMessage("§eUser UUID: " + sb.toString());
        }
    }
}
