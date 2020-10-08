package com.nextplugin.nextmarket.parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugin.nextmarket.api.category.Category;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Singleton
public final class CategoryParser {

    @Inject private Logger logger;
    @Inject private CategoryIconParser categoryIconParser;

    public Category parseSection(ConfigurationSection section) {
        return Category.builder()
                .id(section.getName())
                .displayName(section.getString("displayName"))
                .description(section.getString("description"))
                .icon(this.categoryIconParser.parseSection(section.getConfigurationSection("icon")))
                .build();
    }

    private Set<Material> parseMaterialsSection(ConfigurationSection section) {
        return section.getStringList("allowedMaterials").stream()
                .map(materialName -> {
                    try {
                        return Material.valueOf(materialName);
                    } catch (IllegalArgumentException e) {
                        this.logger.warning("\"" + materialName + "\" material is invalid in categories configuration!");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

}
