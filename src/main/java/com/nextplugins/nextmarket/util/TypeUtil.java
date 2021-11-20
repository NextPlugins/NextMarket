package com.nextplugins.nextmarket.util;

import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.model.product.MaterialData;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class TypeUtil {

    public static ItemStack convertFromLegacy(String materialName, int damage) {
        try {
            return new ItemStack(Material.valueOf(materialName), 1, (short) damage);
        } catch (Exception exception) {
            try {
                val material = Material.valueOf("LEGACY_" + materialName);
                return new ItemStack(Bukkit.getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(material, (byte) damage)));
            } catch (Exception error) {
                NextMarket.getInstance().getLogger().warning("O material " + materialName + " é nulo, verifique a categories.yml");
                return null;
            }
        }
    }

    public static MaterialData convertFromLegacy(String materialData) {
        String materialName = materialData;
        int data = 0;
        boolean ignoreData = true;

        if (materialData.contains(":")) {
            val args = materialData.split(":");

            val type = args[1];
            if (!type.equalsIgnoreCase("all")) {
            	data = Integer.parseInt(type);
            	ignoreData = false;
            }

            materialName = args[0];
        }

        val itemStack = convertFromLegacy(materialName, data);
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            return MaterialData.of(itemStack, ignoreData);
        }

        return null;
    }

}
