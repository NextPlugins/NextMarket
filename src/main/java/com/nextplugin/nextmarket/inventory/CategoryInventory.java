package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.paged.PagedInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.paged.PagedViewer;
import com.nextplugin.nextmarket.api.category.Category;
import com.nextplugin.nextmarket.api.event.MarketItemBuyEvent;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.configuration.InventoryConfiguration;
import com.nextplugin.nextmarket.manager.CategoryManager;
import com.nextplugin.nextmarket.util.ItemBuilder;
import com.nextplugin.nextmarket.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class CategoryInventory extends PagedInventory {

    private final MarketCache marketCache;
    private final CategoryManager categoryManager;

    public CategoryInventory(MarketCache marketCache, CategoryManager categoryManager) {
        super("nextmarket.category",
                InventoryConfiguration.get(InventoryConfiguration::categoryInventoryTitle),
                InventoryLine.valueOf(InventoryConfiguration.get(InventoryConfiguration::categoryInventoryLines)));
        this.marketCache = marketCache;
        this.categoryManager = categoryManager;
    }

    @Override
    protected void onCreate(PagedViewer viewer) {

        Category category = categoryManager.getCategoryMap().get(viewer.getProperty("category").toString());

        viewer.setInventoryTitle(category.getInventoryName().replace("&", "§"));
        viewer.setBackInventory("nextmarket.market");

        viewer.setNextPageItemSlot(45);
        viewer.setPreviousPageItemSlot(53);

    }

    @Override
    protected void onOpen(PagedViewer viewer, InventoryEditor editor) {

        editor.setItem(49, new InventoryItem(
                new ItemBuilder(Material.ARROW)
                        .name("§cVoltar")
                        .flag(ItemFlag.values())
                        .build())
                .addDefaultCallback(click -> viewer.openBackInventory()));

        final Category category = categoryManager.getCategoryMap().get(viewer.getProperty("category").toString());

        final ItemStack itemStack = category.getIcon().getItemStack();

        editor.setItem(53, new InventoryItem(itemStack).addDefaultCallback(click -> viewer.updatePagesItems()));

    }

    @Override
    protected void onUpdate(PagedViewer viewer, InventoryEditor editor) {

    }

    @Override
    public List<InventoryItem> getPagesItems(PagedViewer viewer) {

        List<InventoryItem> items = new LinkedList<>();

        Category category = categoryManager.getCategoryMap().get(viewer.getProperty("category").toString());

        for (MarketItem marketItem : this.marketCache.getMarketCache()) {

            if (category.getAllowedMaterials().contains(marketItem.getItemStack().getType())
                    && !marketItem.isExpired()
                    && marketItem.getDestinationId() == null) {

                ItemStack itemStack = marketItem.getItemStack().clone();
                ItemMeta itemMeta = itemStack.getItemMeta();

                List<String> lore;

                if (itemMeta.hasLore())
                    lore = itemMeta.getLore();
                else
                    lore = new ArrayList<>();

                lore.addAll(InventoryConfiguration.get(InventoryConfiguration::categoryInventoryItemLore)
                        .stream()
                        .map(s -> s
                                .replace("%seller%", marketItem.getSeller().getName())
                                .replace("%price%", NumberUtil.formatNumber(marketItem.getPrice())))
                        .collect(Collectors.toList()));

                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                items.add(new InventoryItem(itemStack).addDefaultCallback(click -> {

                    // TODO temporary for tests

                    Bukkit.getPluginManager().callEvent(new MarketItemBuyEvent(viewer.getPlayer(), marketItem));
                    this.updateInventory(viewer);

                }));

            }

        }

        return items;
    }
}
