package com.nextplugin.nextmarket.manager;

import com.nextplugin.nextmarket.api.category.Category;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class CategoryManager {

    private final Map<String, Category> categoryMap = new LinkedHashMap<>();

    public void registerCategory(Category category) {
        this.categoryMap.put(category.getId(), category);
    }

    public Optional<Category> findCategoryById(String id) {
        return Optional.ofNullable(this.categoryMap.get(id));
    }

    public Optional<Category> findCategoryByFilter(Predicate<Category> filter) {
         return this.categoryMap.values().stream().filter(filter).findFirst();
    }

}
