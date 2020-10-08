package com.nextplugin.nextmarket.parser;

import com.nextplugin.nextmarket.api.category.Category;
import com.nextplugin.nextmarket.api.category.icon.CategoryIcon;
import com.nextplugin.nextmarket.api.item.MarketItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class CategoryParser {

    private Category parseSection(ConfigurationSection section) {
        return Category.builder()
                .id(section.getName())
                .displayName(section.getString("displayName"))
                .description(section.getString("description"))
                .build();
    }

}
