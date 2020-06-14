package cn.newcraft.system.bukkit.command.admin;

import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.util.Method;
import cn.newcraft.system.bukkit.util.interact.ActionBarUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class Wget extends CommandManager {

    public Wget() {
        super("wget", "下载文件", "/wget <URL> <目标文件夹>", "download");
    }

    @Cmd(arg = "<value> <value>", perm = "ncs.command.wget", only = CommandOnly.PLAYER)
    public void download(CommandSender sender, String[] args){
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> downloadFile((Player) sender, args[0], new File(args[1])));
    }

    public void downloadFile(Player p, String urlStr, File dest) {
        try {
            URL url = new URL(urlStr);
            InputStream is = url.openStream();
            File finalDest = new File(dest + "/" + url.getFile());
            finalDest.getParentFile().mkdirs();
            finalDest.createNewFile();
            OutputStream os = new FileOutputStream(finalDest);
            byte data[] = new byte[1024];
            int totalCount = 0;
            int count;
            HttpURLConnection conn = (HttpURLConnection) (url).openConnection();
            int size = conn.getContentLength();
            conn.disconnect();
            while ((count = is.read(data)) != -1) {
                os.write(data, 0, count);
                totalCount += count;
                Main.getNMS().sendActionBar(p, Method.getProgressBar(totalCount, size, 60, "|", "§b", "§7") + " §e(" + Method.getPercent(totalCount, size) + ")");
            }
            p.sendMessage("§a下载完成！");
            os.flush();
            is.close();
            os.close();
        } catch (Exception ex) {
            p.sendMessage("§c无法下载此文件！");
        }
    }

    public static String getPrintSize(long size) {
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}
