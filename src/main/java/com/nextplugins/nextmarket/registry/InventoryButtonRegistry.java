package com.nextplugins.nextmarket.registry;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.nextplugins.nextmarket.api.model.button.InventoryButton;
import com.nextplugins.nextmarket.parser.InventoryButtonParser;
import org.bukkit.configuration.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Henry Fábio
 */
@Singleton
public final class InventoryButtonRegistry {

    private final Map<String, InventoryButton> inventoryButtonMap = new LinkedHashMap<>();

    @Inject @Named("categories") private Configuration categoriesConfig;
    @Inject private InventoryButtonParser inventoryButtonParser;

    public void init() {
        register("main.personalMarket", inventoryButtonParser.parse(
                categoriesConfig.getConfigurationSection("inventory.main.buttons.personalMarket")
        ));
        register("main.announcedItems", inventoryButtonParser.parse(
                categoriesConfig.getConfigurationSection("inventory.main.buttons.announcedItems")
        ));
    }

    public void register(String id, InventoryButton inventoryButton) {
        inventoryButtonMap.put(id, inventoryButton);
    }

    public InventoryButton get(String id) {
        return inventoryButtonMap.get(id);
    }

}
