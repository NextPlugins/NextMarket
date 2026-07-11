package com.nextplugins.nextmarket.manager;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.MaterialData;
import com.nextplugins.nextmarket.parser.CategoryParser;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.val;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public final class CategoryManager {

    private final Map<String, Category> categoryMap = new LinkedHashMap<String, Category>();
    private Category trashTableCategory;

    @Inject @Named("categories") private Configuration categoriesConfiguration;
    @Inject private CategoryParser categoryParser;

    public void init() {
        ConfigurationSection categoriesSection = categoriesConfiguration.getConfigurationSection("categories");
        if (categoriesSection == null) return;

        for (String categoryId : categoriesSection.getKeys(false)) {
            val category = categoryParser.parse(categoriesSection.getConfigurationSection(categoryId));
            if (category == null) continue;

            if (category.isTrashTable()) trashTableCategory = category;
            registerCategory(category);
        }
    }

    public void registerCategory(Category category) {
        this.categoryMap.put(category.getId(), category);
    }

    public Optional<Category> findCategoryById(String id) {
        return Optional.ofNullable(this.categoryMap.get(id));
    }

    public Optional<Category> findCategory(ItemStack itemStack) {
        val materialData = MaterialData.of(itemStack, false);
        val itemMeta = itemStack.getItemMeta();

        NBTItem nbtItem = null;
        try {
            nbtItem = new NBTItem(itemStack);
        } catch (Throwable ignored) {
            // NBT-API may fail on edge cases; continue with material/name matching
        }

        for (Category category : categoryMap.values()) {
            if (category.isTrashTable()) continue;

            val configuration = category.getConfiguration();

            if (nbtItem != null) {
                for (String nbt : configuration.getNbts()) {
                    if (nbt != null && !nbt.isEmpty() && nbtItem.hasKey(nbt)) {
                        return Optional.of(category);
                    }
                }
            }

            for (MaterialData material : configuration.getMaterials()) {
                if (material.matches(itemStack) || material.equals(materialData)) {
                    return Optional.of(category);
                }
            }

            for (String name : configuration.getNames()) {
                if (itemMeta != null
                        && itemMeta.hasDisplayName()
                        && itemMeta.getDisplayName() != null
                        && itemMeta.getDisplayName().equalsIgnoreCase(name)) {
                    return Optional.of(category);
                }
            }
        }

        if (trashTableCategory == null) return Optional.empty();
        return Optional.of(trashTableCategory);
    }

    public Map<String, Category> getCategoryMap() {
        return ImmutableMap.copyOf(this.categoryMap);
    }

}
