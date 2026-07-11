package com.nextplugins.nextmarket.util;

import com.nextplugins.nextmarket.api.model.product.MaterialData;
import com.nextplugins.nextmarket.compat.Items;
import org.bukkit.inventory.ItemStack;

/**
 * @deprecated Use {@link Items} instead. Kept for binary compatibility of any external callers.
 */
@Deprecated
public final class TypeUtil {

    public static ItemStack convertFromLegacy(String materialName, int damage) {
        return Items.create(materialName, damage);
    }

    public static MaterialData convertFromLegacy(String materialData) {
        return Items.parseMaterialData(materialData);
    }

}
