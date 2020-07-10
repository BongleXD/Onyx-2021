package cn.newcraft.system.bungee.task;

import cn.newcraft.system.bungee.config.BroadcastConfig;
import net.md_5.bungee.BungeeCord;

public class BroadcastTask {

    private Thread thread;

    public BroadcastTask(){
        thread = new Thread(() -> {
            while (true) {
                BroadcastConfig.messageMap.forEach((s, messages) -> {
                    try {
                        Thread.sleep(BroadcastConfig.duration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    BungeeCord.getInstance().getPlayers().forEach(online -> {
                        messages.forEach(online::sendMessage);
                    });
                });
            }
        });
        thread.start();
    }

}
