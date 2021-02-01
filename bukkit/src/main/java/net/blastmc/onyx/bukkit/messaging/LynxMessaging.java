package net.blastmc.onyx.bukkit.messaging;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.utils.IrcUtil;
import net.blastmc.onyx.bukkit.config.SettingConfig;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class LynxMessaging implements PluginMessageListener {

    public LynxMessaging(){
        Bukkit.getMessenger().registerIncomingPluginChannel(Main.getInstance(), "Lynx", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] b) {
        if (!channel.equals("Lynx")){
            return;
        }
        if (!SettingConfig.LYNX){
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(b);
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("CheckLynx")){
            if (p.hasPermission("Lynx.staff")){
                IrcUtil.sendIRCMessage(p.getDisplayName() + " §e加入了 BlastMC 服务器！");
            } else {
                Main.getNMS().crashClient(p);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    IrcUtil.sendIRCMessage(p.getDisplayName() + " §c由于非法安装了 Lynx 已经被崩溃客户端，请及时调查！");
                    p.kickPlayer("与服务器断开连接");
                }, 30L);
            }
        }
    }
}
