package com.nextplugins.nextmarket.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReflectionUtils {

    private static String versionString;
    private static Map<String, Class<?>> loadedNMSClasses;
    private static Map<String, Class<?>> loadedOBCClasses;
    private static Map<Class<?>, Map<String, Method>> loadedMethods;
    private static Map<Class<?>, Map<String, Field>> loadedFields;

    static {
        ReflectionUtils.loadedNMSClasses = new HashMap<>();
        ReflectionUtils.loadedOBCClasses = new HashMap<>();
        ReflectionUtils.loadedMethods = new HashMap<>();
        ReflectionUtils.loadedFields = new HashMap<>();
    }

    public static String getVersion() {
        if (ReflectionUtils.versionString == null) {
            final String name = Bukkit.getServer().getClass().getPackage().getName();
            ReflectionUtils.versionString = name.substring(name.lastIndexOf(46) + 1) + ".";
        }
        return ReflectionUtils.versionString;
    }

    public static int getVersionNumber() {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(getVersion());

        StringBuilder versionString = new StringBuilder();

        while (matcher.find()) {
            versionString.append(matcher.group());
        }

        if (versionString.length() == 3) {
            return Integer.parseInt(versionString.substring(0, 2));
        } else {
            return Integer.parseInt(versionString.substring(0, 3));
        }
    }

    public static Class<?> getNMSClass(final String nmsClassName) {
        if (ReflectionUtils.loadedNMSClasses.containsKey(nmsClassName)) {
            return ReflectionUtils.loadedNMSClasses.get(nmsClassName);
        }
        final String clazzName = "net.minecraft.server." + getVersion() + nmsClassName;
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (Throwable t) {
            t.printStackTrace();
            return ReflectionUtils.loadedNMSClasses.put(nmsClassName, null);
        }
        ReflectionUtils.loadedNMSClasses.put(nmsClassName, clazz);
        return clazz;
    }

    public static synchronized Class<?> getOBCClass(final String obcClassName) {
        if (ReflectionUtils.loadedOBCClasses.containsKey(obcClassName)) {
            return ReflectionUtils.loadedOBCClasses.get(obcClassName);
        }
        final String clazzName = "org.bukkit.craftbukkit." + getVersion() + obcClassName;
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (Throwable t) {
            t.printStackTrace();
            ReflectionUtils.loadedOBCClasses.put(obcClassName, null);
            return null;
        }
        ReflectionUtils.loadedOBCClasses.put(obcClassName, clazz);
        return clazz;
    }

    public static Object getConnection(final Player player) {
        final Method getHandleMethod = getMethod(player.getClass(), "getHandle", (Class<?>[]) new Class[0]);
        if (getHandleMethod != null) {
            try {
                final Object nmsPlayer = getHandleMethod.invoke(player);
                final Field playerConField = getField(nmsPlayer.getClass(), "playerConnection");
                assert playerConField != null;
                return playerConField.get(nmsPlayer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... params) {
        try {
            return clazz.getConstructor(params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... params) {
        if (!ReflectionUtils.loadedMethods.containsKey(clazz)) {
            ReflectionUtils.loadedMethods.put(clazz, new HashMap<String, Method>());
        }
        final Map<String, Method> methods = ReflectionUtils.loadedMethods.get(clazz);
        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }
        try {
            final Method method = clazz.getMethod(methodName, params);
            methods.put(methodName, method);
            ReflectionUtils.loadedMethods.put(clazz, methods);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
            methods.put(methodName, null);
            ReflectionUtils.loadedMethods.put(clazz, methods);
            return null;
        }
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        if (!ReflectionUtils.loadedFields.containsKey(clazz)) {
            ReflectionUtils.loadedFields.put(clazz, new HashMap<String, Field>());
        }
        final Map<String, Field> fields = ReflectionUtils.loadedFields.get(clazz);
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }
        try {
            final Field field = clazz.getField(fieldName);
            fields.put(fieldName, field);
            ReflectionUtils.loadedFields.put(clazz, fields);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            fields.put(fieldName, null);
            ReflectionUtils.loadedFields.put(clazz, fields);
            return null;
        }
    }

}
