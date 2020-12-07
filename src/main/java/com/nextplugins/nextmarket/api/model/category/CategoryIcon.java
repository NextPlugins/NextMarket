package com.nextplugins.nextmarket.api.model.category;

import lombok.Builder;
import lombok.Data;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 * @author Henry Fábio
 */
@Builder
@Data
public final class CategoryIcon {

    private final MaterialData materialData;
    private final boolean enchant;
    private final int inventorySlot;

}
