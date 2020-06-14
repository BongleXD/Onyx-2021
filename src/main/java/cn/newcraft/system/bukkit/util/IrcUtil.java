package cn.newcraft.system.bukkit.util;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IrcUtil {

    private static PrintWriter pw;
    private BufferedReader br;
    private Socket socket;

    public IrcUtil() {
        new connect().start();
    }

    public static void sendIRCMessage(String message) {
        pw.println(message);
        pw.flush();
    }

    private class connect extends Thread {
        @Override
        public void run() {
            this.setName("Connect");
            try {
                socket = new Socket("localhost", 36102);
                pw = new PrintWriter(socket.getOutputStream(), true);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Throwable e) {
                Bukkit.getConsoleSender().sendMessage("§cIRC发送信息模块已断线，正在尝试重连");
                new connect().start();
            }
        }
    }

}
