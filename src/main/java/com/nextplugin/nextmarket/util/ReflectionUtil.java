package com.nextplugin.nextmarket.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtil {

    private static String versionString;
    private static Map<String, Class<?>> loadedNMSClasses;
    private static Map<String, Class<?>> loadedOBCClasses;
    private static Map<Class<?>, Map<String, Method>> loadedMethods;
    private static Map<Class<?>, Map<String, Field>> loadedFields;

    static {
        ReflectionUtil.loadedNMSClasses = new HashMap<>();
        ReflectionUtil.loadedOBCClasses = new HashMap<>();
        ReflectionUtil.loadedMethods = new HashMap<>();
        ReflectionUtil.loadedFields = new HashMap<>();
    }

    public static String getVersion() {
        if (ReflectionUtil.versionString == null) {
            final String name = Bukkit.getServer().getClass().getPackage().getName();
            ReflectionUtil.versionString = name.substring(name.lastIndexOf(46) + 1) + ".";
        }
        return ReflectionUtil.versionString;
    }

    public static Class<?> getNMSClass(final String nmsClassName) {
        if (ReflectionUtil.loadedNMSClasses.containsKey(nmsClassName)) {
            return ReflectionUtil.loadedNMSClasses.get(nmsClassName);
        }
        final String clazzName = "net.minecraft.server." + getVersion() + nmsClassName;
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (Throwable t) {
            t.printStackTrace();
            return ReflectionUtil.loadedNMSClasses.put(nmsClassName, null);
        }
        ReflectionUtil.loadedNMSClasses.put(nmsClassName, clazz);
        return clazz;
    }

    public static synchronized Class<?> getOBCClass(final String obcClassName) {
        if (ReflectionUtil.loadedOBCClasses.containsKey(obcClassName)) {
            return ReflectionUtil.loadedOBCClasses.get(obcClassName);
        }
        final String clazzName = "org.bukkit.craftbukkit." + getVersion() + obcClassName;
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (Throwable t) {
            t.printStackTrace();
            ReflectionUtil.loadedOBCClasses.put(obcClassName, null);
            return null;
        }
        ReflectionUtil.loadedOBCClasses.put(obcClassName, clazz);
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
        if (!ReflectionUtil.loadedMethods.containsKey(clazz)) {
            ReflectionUtil.loadedMethods.put(clazz, new HashMap<String, Method>());
        }
        final Map<String, Method> methods = ReflectionUtil.loadedMethods.get(clazz);
        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }
        try {
            final Method method = clazz.getMethod(methodName, params);
            methods.put(methodName, method);
            ReflectionUtil.loadedMethods.put(clazz, methods);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
            methods.put(methodName, null);
            ReflectionUtil.loadedMethods.put(clazz, methods);
            return null;
        }
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        if (!ReflectionUtil.loadedFields.containsKey(clazz)) {
            ReflectionUtil.loadedFields.put(clazz, new HashMap<String, Field>());
        }
        final Map<String, Field> fields = ReflectionUtil.loadedFields.get(clazz);
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }
        try {
            final Field field = clazz.getField(fieldName);
            fields.put(fieldName, field);
            ReflectionUtil.loadedFields.put(clazz, fields);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            fields.put(fieldName, null);
            ReflectionUtil.loadedFields.put(clazz, fields);
            return null;
        }
    }
}
