package com.nextplugins.nextmarket.parser;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.category.CategoryConfiguration;
import com.nextplugins.nextmarket.api.model.category.CategoryIcon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;

import java.util.stream.Collectors;

/**
 * @author Henry Fábio
 */
@Singleton
public final class CategoryParser {

    public Category parse(ConfigurationSection section) {
        return Category.builder()
                .id(section.getName())
                .displayName(colors(section.getString("displayName")))
                .description(section.getStringList("description").stream()
                        .map(this::colors)
                        .collect(Collectors.toList())
                )
                .icon(this.parseCategoryIcon(section.getConfigurationSection("icon")))
                .configuration(this.parseCategoryConfiguration(section.getConfigurationSection("configuration")))
                .build();
    }

    private CategoryIcon parseCategoryIcon(ConfigurationSection section) {
        return CategoryIcon.builder()
                .materialData(new MaterialData(
                        Material.getMaterial(section.getString("material")),
                        (byte) section.getInt("data")
                ))
                .enchant(section.getBoolean("enchant"))
                .inventorySlot(section.getInt("inventorySlot"))
                .build();
    }

    private CategoryConfiguration parseCategoryConfiguration(ConfigurationSection section) {
        return CategoryConfiguration.builder()
                .inventoryTitle(section.getString("inventoryTitle"))
                .materials(section.getStringList("materials").stream()
                        .map(Material::getMaterial)
                        .collect(Collectors.toList())
                )
                .build();
    }

    private String colors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
