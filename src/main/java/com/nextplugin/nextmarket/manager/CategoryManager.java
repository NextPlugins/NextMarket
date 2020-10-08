package com.nextplugin.nextmarket.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugin.nextmarket.api.category.Category;
import com.nextplugin.nextmarket.parser.CategoryParser;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Getter
@Singleton
public final class CategoryManager {

    private final Map<String, Category> categoryMap = new LinkedHashMap<>();

    @Inject private CategoryParser categoryParser;

    public void registerCategory(Category category) {
        this.categoryMap.put(category.getId(), category);
    }

    public void registerCategories(ConfigurationSection section) {
        for (String sectionName : section.getKeys(false)) {
            ConfigurationSection categorySection = section.getConfigurationSection(sectionName);
            registerCategory(this.categoryParser.parseSection(categorySection));
        }
    }

    public Optional<Category> findCategoryById(String id) {
        return Optional.ofNullable(this.categoryMap.get(id));
    }

    public Optional<Category> findCategoryByFilter(Predicate<Category> filter) {
         return this.categoryMap.values().stream().filter(filter).findFirst();
    }

}
