package com.nextplugins.nextmarket.manager;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.parser.CategoryParser;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
public final class CategoryManager {

    private final Map<String, Category> categoryMap = new LinkedHashMap<>();

    @Inject @Named("main") private Logger logger;
    @Inject @Named("categories") private Configuration categoriesConfiguration;

    @Inject private CategoryParser categoryParser;

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

    public Optional<Category> findCategoryByMaterial(MaterialData materialData) {
        return categoryMap.values().stream()
                .filter(category -> category.getConfiguration().getMaterials().contains(materialData))
                .findFirst();
    }

    public Map<String, Category> getCategoryMap() {
        return ImmutableMap.copyOf(this.categoryMap);
    }

}
