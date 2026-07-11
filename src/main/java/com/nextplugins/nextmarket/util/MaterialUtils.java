package com.nextplugins.nextmarket.util;

import com.nextplugins.nextmarket.compat.Items;
import org.bukkit.inventory.ItemStack;

/**
 * @deprecated Use {@link Items} instead.
 */
@Deprecated
public class MaterialUtils {

    public static ItemStack convertFromLegacy(String materialName, int damage) {
        return Items.create(materialName, damage);
    }

}
