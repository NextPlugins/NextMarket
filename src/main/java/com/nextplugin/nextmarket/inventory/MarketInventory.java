package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.global.GlobalInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.IViewer;
import com.nextplugin.nextmarket.api.item.MenuIcon;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.configuration.InventoryConfiguration;
import com.nextplugin.nextmarket.manager.CategoryManager;
import com.nextplugin.nextmarket.util.ItemBuilder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class MarketInventory extends GlobalInventory {

    private final CategoryManager categoryManager;
    private final MarketCache marketCache;

    public MarketInventory(CategoryManager categoryManager, MarketCache marketCache) {

        super("nextmarket.market",
                InventoryConfiguration.get(InventoryConfiguration::mainInventoryTitle),
                InventoryLine.valueOf(InventoryConfiguration.get(InventoryConfiguration::mainInventoryLines))
        );

        this.categoryManager = categoryManager;
        this.marketCache = marketCache;
    }

    @Override
    protected void onCreate(InventoryEditor editor) {

        categoryManager.getCategoryMap().forEach((s, category) -> {

            MenuIcon icon = category.getIcon();

            List<String> lore = new ArrayList<>();

            List<MarketItem> collect = new ArrayList<>();
            for (MarketItem marketItem : marketCache.getMarketCache()) {
                if (category.getAllowedMaterials().contains(marketItem.getItemStack().getType())) {
                    collect.add(marketItem);
                }
            }

            String suffix = collect.size() > 1 ? " itens" : " item";

            for (String string : category.getDescription()) {
                lore.add(string
                        .replace("&", "§")
                        .replace("%amount%", collect.size() + suffix));
            }

            ItemStack itemStack = new ItemBuilder(icon.getItemStack().getType())
                    .amount(1)
                    .durability(icon.getItemStack().getDurability())
                    .name(category.getDisplayName()
                            .replace("&", "§")
                            .replace("%amount%", collect.size() + suffix))
                    .lore(lore)
                    .flag(ItemFlag.values())
                    .build();

            editor.setItem(icon.getPosition(), new InventoryItem(itemStack).addDefaultCallback(click -> {

                CategoryInventory categoryInventory = new CategoryInventory(marketCache, categoryManager);

                categoryInventory.openInventory(click.getPlayer(), viewer -> viewer.setProperty("category", category.getId()));

            }));

        });

    }

    @Override
    protected void onOpen(IViewer viewer, InventoryEditor editor) {

    }

    @Override
    protected void onUpdate(InventoryEditor editor) {

    }

}
