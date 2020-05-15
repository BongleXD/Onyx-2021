package cn.newcraft.system.bukkit.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import cn.newcraft.system.bukkit.Main;
import org.bukkit.Bukkit;

public class ReflectUtils {

    public static void setField(Class<?> clazz, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
        Field f = getField(clazz, fieldName);
        f.set(clazz, value);
        f.setAccessible(false);
    }

    public static void setField(Object obj, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
        Field f = getField(obj.getClass(), fieldName);
        f.set(obj, value);
        f.setAccessible(false);
    }

    private static Method getMethod(Class<?> clazz, String mname) {
        Method m;
        try {
            m = clazz.getDeclaredMethod(mname);
        } catch (Exception e) {
            try {
                m = clazz.getMethod(mname);
            } catch (Exception ex) {
                return null;
            }
        }
        m.setAccessible(true);
        return m;
    }

    public static Enum<?> getEnum(Class<?> clazz, String constant) throws Exception {
        Class<?> c = Class.forName(clazz.getName());
        Enum<?>[] econstants = (Enum<?>[]) c.getEnumConstants();
        for (Enum<?> e : econstants)
            if (e.name().equalsIgnoreCase(constant))
                return e;
        throw new Exception("Enum constant not found " + constant);
    }

    public static Enum<?> getEnum(Class<?> clazz, String enumname, String constant) throws Exception {
        Class<?> c = Class.forName(clazz.getName() + "$" + enumname);
        Enum<?>[] econstants = (Enum<?>[]) c.getEnumConstants();
        for (Enum<?> e : econstants)
            if (e.name().equalsIgnoreCase(constant))
                return e;
        throw new Exception("Enum constant not found " + constant);
    }

    public static Object getObject(Class<?> clazz, Object obj, String fname) throws Exception {
        return getField(clazz, fname).get(obj);
    }

    public static Object getObject(Object obj, String fname) throws Exception {
        return getField(obj.getClass(), fname).get(obj);
    }

    private static Method getMethod(Class<?> clazz, String mname, Class<?>... args) {
        Method m;
        try {
            m = clazz.getDeclaredMethod(mname, args);
        } catch (Exception e) {
            try {
                m = clazz.getMethod(mname, args);
            } catch (Exception ex) {
                return null;
            }
        }
        m.setAccessible(true);
        return m;
    }

    public static Class<?> getBukkitClass(String clazz) throws Exception {
        return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + clazz);
    }

    public static Class<?> getBungeeClass(String path, String clazz) throws Exception {
        return Class.forName("net.md_5.bungee." + path + "." + clazz);
    }

    private static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) throws Exception {
        Constructor<?> c = clazz.getConstructor(args);
        c.setAccessible(true);
        return c;
    }

    public static Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object... initargs) throws Exception {
        return getConstructor(clazz, args).newInstance(initargs);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method) throws Exception {
        return Objects.requireNonNull(getMethod(clazz, method)).invoke(obj);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Class<?>[] args, Object... initargs)
            throws Exception {
        return Objects.requireNonNull(getMethod(clazz, method, args)).invoke(obj, initargs);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Object... initargs) throws Exception {
        return Objects.requireNonNull(getMethod(clazz, method)).invoke(obj, initargs);
    }

    public static Object invokeMethod(Object obj, String method) throws Exception {
        return Objects.requireNonNull(getMethod(obj.getClass(), method)).invoke(obj);
    }

    public static Object invokeMethod(Object obj, String method, Object[] initargs) throws Exception {
        return Objects.requireNonNull(getMethod(obj.getClass(), method)).invoke(obj, initargs);
    }

    public static Field getFirstFieldByType(Class<?> clazz, Class<?> type) {
        for(Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.getType() == type)
                return field;
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Field f = null;
        try {
            f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    public static Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getCraftClass(String className) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}
