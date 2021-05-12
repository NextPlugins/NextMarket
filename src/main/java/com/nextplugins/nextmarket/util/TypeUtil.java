package com.nextplugins.nextmarket.util;

import org.bukkit.Material;

public final class TypeUtil {

    public static Material getType(String name) {
        try {
            return Material.getMaterial(name, true);
        } catch (Error | Exception e) {
            return Material.getMaterial(name);
        }
    }

}
