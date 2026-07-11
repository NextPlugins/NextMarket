package com.nextplugins.nextmarket.parser;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.category.CategoryConfiguration;
import com.nextplugins.nextmarket.api.model.category.CategoryIcon;
import com.nextplugins.nextmarket.api.model.product.MaterialData;
import com.nextplugins.nextmarket.compat.Items;
import com.nextplugins.nextmarket.util.MessageUtils;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Singleton
public final class CategoryParser {

    public Category parse(ConfigurationSection section) {
        val iconSection = section.getConfigurationSection("icon");
        val configurationSection = section.getConfigurationSection("configuration");
        if (iconSection == null || configurationSection == null) {
            NextMarket.getInstance().getLogger().warning(
                    "A categoria " + section.getName() + " tem um problema de configuração. (section de ícone ou configuração inválida)"
            );
            return null;
        }

        return Category.builder()
                .id(section.getName())
                .trashTable(section.getBoolean("trashTableCategory", false))
                .displayName(MessageUtils.colored(section.getString("displayName")))
                .description(section.getStringList("description").stream()
                        .map(MessageUtils::colored)
                        .collect(Collectors.toList()))
                .icon(this.parseCategoryIcon(iconSection))
                .configuration(this.parseCategoryConfiguration(configurationSection))
                .build();
    }

    private CategoryIcon parseCategoryIcon(ConfigurationSection section) {
        String materialName = section.getString("material", "BARRIER");
        int data = section.getInt("data", 0);

        MaterialData materialData = Items.parseMaterialData(
                data > 0 ? materialName + ":" + data : materialName
        );
        if (materialData == null) {
            materialData = Items.parseMaterialData(materialName);
        }
        if (materialData == null) {
            materialData = new MaterialData(com.cryptomorin.xseries.XMaterial.BARRIER, null, true);
        }

        return CategoryIcon.builder()
                .materialData(materialData)
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
                        .map(MessageUtils::colored)
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .materials(section.getStringList("materials").stream()
                        .map(Items::parseMaterialData)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .nbts(section.contains("nbts") ? section.getStringList("nbts")
                        .stream()
                        .filter(not(CategoryParser::isBlank))
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

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
