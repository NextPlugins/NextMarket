package com.nextplugins.nextmarket.util;

import com.nextplugins.nextmarket.NextMarket;
import org.bukkit.Material;

public final class TypeUtil {

    public static Material getType(String name) {

        NextMarket.getInstance().getLogger().info("Trying to find material by name: " + name);

        try {
            return Material.getMaterial(name, true);
        } catch (Error | Exception e) {
            return Material.getMaterial(name);
        }
    }

}
