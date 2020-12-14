package com.nextplugins.nextmarket.registry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.nextplugins.nextmarket.inventory.button.InventoryButton;
import com.nextplugins.nextmarket.parser.InventoryButtonParser;
import org.bukkit.configuration.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public final class InventoryButtonRegistry {

    private final Map<String, InventoryButton> inventoryButtonMap = new LinkedHashMap<>();

    @Inject @Named("categories") private Configuration categoriesConfig;
    @Inject private InventoryButtonParser inventoryButtonParser;

    public void init() {
        register("main.personalMarket", inventoryButtonParser.parse(
                categoriesConfig.getConfigurationSection("inventory.main.buttons.personalMarket")
        ));
        register("main.sellingMarket", inventoryButtonParser.parse(
                categoriesConfig.getConfigurationSection("inventory.main.buttons.sellingMarket")
        ));
        register("category.update", inventoryButtonParser.parse(
                categoriesConfig.getConfigurationSection("inventory.category.buttons.update")
        ));
        register("personal.update", inventoryButtonParser.parse(
                categoriesConfig.getConfigurationSection("inventory.personal.buttons.update")
        ));
        register("selling.update", inventoryButtonParser.parse(
                categoriesConfig.getConfigurationSection("inventory.selling.buttons.update")
        ));
    }

    public void register(String id, InventoryButton inventoryButton) {
        inventoryButtonMap.put(id, inventoryButton);
    }

    public InventoryButton get(String id) {
        return inventoryButtonMap.get(id);
    }

}
