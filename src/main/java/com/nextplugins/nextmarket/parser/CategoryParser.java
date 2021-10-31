package com.nextplugins.nextmarket.parser;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.category.CategoryConfiguration;
import com.nextplugins.nextmarket.api.model.category.CategoryIcon;
import com.nextplugins.nextmarket.api.model.product.MaterialData;
import com.nextplugins.nextmarket.util.ColorUtils;
import com.nextplugins.nextmarket.util.TypeUtil;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Singleton
public final class CategoryParser {

    public Category parse(ConfigurationSection section) {
        return Category.builder()
                .id(section.getName())
                .displayName(ColorUtils.format(section.getString("displayName")))
                .description(section.getStringList("description").stream()
                        .map(ColorUtils::format)
                        .collect(Collectors.toList()))
                .icon(this.parseCategoryIcon(section.getConfigurationSection("icon")))
                .configuration(this.parseCategoryConfiguration(section.getConfigurationSection("configuration")))
                .build();
    }

    private CategoryIcon parseCategoryIcon(ConfigurationSection section) {
        val itemStack = TypeUtil.convertFromLegacy(
                section.getString("material"),
                (byte) section.getInt("data"));

        return CategoryIcon.builder()
                .materialData(itemStack == null ? new MaterialData(Material.BARRIER, 0, false) : MaterialData.of(itemStack, false))
                .enchant(section.getBoolean("enchant"))
                .inventorySlot(section.getInt("inventorySlot"))
                .build();
    }

    private CategoryConfiguration parseCategoryConfiguration(ConfigurationSection section) {
        return CategoryConfiguration.builder()
                .inventoryTitle(section.getString("inventoryTitle"))
                .names(section.contains("names") ? section.getStringList("names")
                        .stream()
                        .filter(not(CategoryParser::isBlank))
                        .map(ColorUtils::format)
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .materials(section.getStringList("materials").stream()
                        .map(TypeUtil::convertFromLegacy)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .build();
    }

    // Apache method
    public static boolean isBlank(CharSequence sequence) {
        val strLen = sequence.length();
        if (strLen == 0) return true;

        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(sequence.charAt(i))) continue;
            return false;
        }

        return true;
    }

    public <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }

}
