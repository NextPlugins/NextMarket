package com.nextplugins.nextmarket.util;

import com.nextplugins.nextmarket.NextMarket;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public final class TypeUtil {

    public static ItemStack convertFromLegacy(String materialName, int damage) {

        try {
            val material = Material.valueOf("LEGACY_" + materialName);
            return new ItemStack(Bukkit.getUnsafe().fromLegacy(new MaterialData(material, (byte) damage)));
        } catch (Exception error) {
            try {
                return new ItemStack(Material.getMaterial(materialName), 1, (short) damage);
            } catch (Exception exception) {
                NextMarket.getInstance().getLogger().warning("Material " + materialName + " is invalid!");
                return null;
            }
        }

    }

    public static MaterialData convertFromLegacy(String materialData) {

        String materialName = materialData;
        int data = 0;

        if (materialData.contains(":")) {

            val args = materialData.split(":");
            data = Integer.parseInt(args[1]);
            materialName = args[0];

        }else data = -1;

        val itemStack = convertFromLegacy(materialName, data);
        if (itemStack != null) return itemStack.getData();
        return null;

    }

}
