package com.nextplugins.nextmarket.manager;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.MaterialData;
import com.nextplugins.nextmarket.parser.CategoryParser;
import lombok.val;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public final class CategoryManager {

    private final Map<String, Category> categoryMap = new LinkedHashMap<>();

    @Inject @Named("categories") private Configuration categoriesConfiguration;

    @Inject
    private CategoryParser categoryParser;

    public void init() {
        ConfigurationSection categoriesSection = categoriesConfiguration.getConfigurationSection("categories");
        for (String categoryId : categoriesSection.getKeys(false)) {
            registerCategory(categoryParser.parse(categoriesSection.getConfigurationSection(categoryId)));
        }
    }

    public void registerCategory(Category category) {
        this.categoryMap.put(category.getId(), category);
    }

    public Optional<Category> findCategoryById(String id) {
        return Optional.ofNullable(this.categoryMap.get(id));
    }

    public Optional<Category> findCategory(ItemStack itemStack) {
        for (Category category : categoryMap.values()) {
            for (String name : category.getConfiguration().getNames()) {
                val itemMeta = itemStack.getItemMeta();
                if (itemMeta != null && itemMeta.getDisplayName() != null && itemMeta.getDisplayName().equalsIgnoreCase(name)) {
                    return Optional.of(category);
                }
            }

            val materialData = MaterialData.of(itemStack, false);
            for (MaterialData material : category.getConfiguration().getMaterials()) {
                if (material.equals(materialData)) {
                    return Optional.of(category);
                }
            }
        }

        return Optional.empty();
    }

    public Map<String, Category> getCategoryMap() {
        return ImmutableMap.copyOf(this.categoryMap);
    }

}
