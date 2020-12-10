package net.blastmc.onyx.bungee.api;

import net.blastmc.onyx.api.util.Method;
import net.blastmc.onyx.bungee.config.SkinConfig;
import com.google.gson.internal.LinkedTreeMap;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.LoginResult;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SkinAPI {

    private static SkinAPI api = new SkinAPI();

    public static SkinAPI getApi(){
        return api;
    }

    public void reloadPlayer(ProxiedPlayer p) {
        String name = p.getName();
        BungeeCord.getInstance().getConsole().sendMessage("Attempting to reload skin for player " + name);
        PendingConnection conn = p.getPendingConnection();
        new Thread(() -> {
            String skin;
            String signature;
            try {
                String reply;
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                String uuid = reply.split("\"id\":\"")[1].split("\"")[0];
                HashMap map = Method.readUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                ArrayList<LinkedTreeMap> list = ((ArrayList) map.get("properties"));
                skin = (String) list.get(0).get("value");
                signature = (String) list.get(0).get("signature");
                SkinConfig.cfg.getYml().set(name + ".value", skin);
                SkinConfig.cfg.getYml().set(name + ".signature", signature);
                SkinConfig.cfg.save();
                SkinConfig.cfg.reload();
            } catch (Exception e) {
                skin = null;
                signature = null;
            }
            if ((skin != null && signature != null)) {
                Class<?> initialHandlerClass = conn.getClass();
                try {
                    Field f = initialHandlerClass.getDeclaredField("loginProfile");
                    LoginResult.Property property = new LoginResult.Property("textures", skin, signature);
                    LoginResult loginResult = new LoginResult(conn.getUniqueId().toString(), conn.getName(), new LoginResult.Property[]{property});
                    if (signature.isEmpty() || skin.isEmpty()) {
                        loginResult = new LoginResult(conn.getUniqueId().toString(), conn.getName(), new LoginResult.Property[]{});
                    }
                    f.setAccessible(true);
                    f.set(conn, loginResult);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setSkin(PendingConnection conn, String name) {
        BungeeCord.getInstance().getConsole().sendMessage("Attempting to change skin... " + conn.getName() + " to " + name);
        new Thread(() -> {
            String skin;
            String signature;
            if(!name.equalsIgnoreCase("steve")){
                if(SkinConfig.cfg.getYml().get(name) == null){
                    try {
                        String reply;
                        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                        String uuid = reply.split("\"id\":\"")[1].split("\"")[0];
                        HashMap map = Method.readUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                        ArrayList<LinkedTreeMap> list = ((ArrayList) map.get("properties"));
                        skin = (String) list.get(0).get("value");
                        signature = (String) list.get(0).get("signature");
                        SkinConfig.cfg.getYml().set(name + ".value", skin);
                        SkinConfig.cfg.getYml().set(name + ".signature", signature);
                        SkinConfig.cfg.save();
                        SkinConfig.cfg.reload();
                    } catch (Exception e) {
                        skin = null;
                        signature = null;
                    }
                }else {
                    skin = SkinConfig.cfg.getYml().getString(name + ".value");
                    signature = SkinConfig.cfg.getYml().getString(name + ".signature");
                }
            }else{
                skin = "";
                signature = "";
            }
            if((skin != null && signature != null)){
                Class<?> initialHandlerClass = conn.getClass();
                try {
                    Field f = initialHandlerClass.getDeclaredField("loginProfile");
                    LoginResult.Property property = new LoginResult.Property("textures", skin, signature);
                    LoginResult loginResult = new LoginResult(conn.getUniqueId().toString(), conn.getName(), new LoginResult.Property[] {property});
                    if(signature.isEmpty() || skin.isEmpty()){
                        loginResult = new LoginResult(conn.getUniqueId().toString(), conn.getName(), new LoginResult.Property[] {});
                    }
                    f.setAccessible(true);
                    f.set(conn, loginResult);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
