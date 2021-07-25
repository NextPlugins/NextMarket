package com.nextplugins.nextmarket.util;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class MaterialUtils {

    public static ItemStack convertFromLegacy(String materialName, int damage) {

        try {
            val material = Material.valueOf("LEGACY_" + materialName);
            return new ItemStack(Bukkit.getUnsafe().fromLegacy(new MaterialData(material, (byte) damage)));
        } catch (Exception error) {
            return new ItemStack(Material.getMaterial(materialName), 1, (short) damage);
        }

    }

}
