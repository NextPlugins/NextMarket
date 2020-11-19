package com.nextplugin.nextmarket.api.item;

import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Builder
@Data
public final class MenuIcon {

    private final ItemStack itemStack;
    private final int position;

}
