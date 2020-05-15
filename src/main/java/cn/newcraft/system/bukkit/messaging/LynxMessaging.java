package cn.newcraft.system.bukkit.messaging;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.config.SettingConfig;
import cn.newcraft.system.bukkit.util.IRCUtils;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.lang.reflect.InvocationTargetException;

public class LynxMessaging implements PluginMessageListener {

    public LynxMessaging(){
        Bukkit.getMessenger().registerIncomingPluginChannel(Main.getInstance(), "Lynx", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] b) {
        if (!channel.equals("Lynx")){
            return;
        }
        if (!SettingConfig.cfg.getYml().getBoolean("Lynx.CheckLynx")){
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(b);
        String subchannel = in.readUTF();
        if (subchannel.equalsIgnoreCase("CheckLynx")){
            if (p.hasPermission("Lynx.staff")){
                IRCUtils.sendIRCMessage(p.getDisplayName() + " §e加入了NewCraft服务器！");
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                    public void run() {
                        IRCUtils.sendIRCMessage(p.getDisplayName() + " §c由于非法安装了Lynx已经被崩溃客户端，请及时调查！");
                        p.kickPlayer("与服务器断开连接");
                    }
                }, 30L);
            }
        }
    }
}
