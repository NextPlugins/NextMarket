package com.nextplugin.nextmarket.api.category.icon;

import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Builder
@Data
public final class CategoryIcon {

    private final ItemStack itemStack;
    private final int position;

}
