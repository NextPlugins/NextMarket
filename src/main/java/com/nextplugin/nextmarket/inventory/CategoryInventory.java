package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.paged.PagedInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.paged.PagedViewer;
import com.nextplugin.nextmarket.api.category.Category;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.configuration.InventoryConfiguration;
import com.nextplugin.nextmarket.manager.CategoryManager;
import com.nextplugin.nextmarket.sql.MarketDAO;
import com.nextplugin.nextmarket.util.ItemBuilder;
import com.nextplugin.nextmarket.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class CategoryInventory extends PagedInventory {

    private final MarketDAO marketDAO;
    private final CategoryManager categoryManager;

    public CategoryInventory(MarketDAO marketDAO, CategoryManager categoryManager) {
        super("nextmarket.category",
                InventoryConfiguration.get(InventoryConfiguration::categoryInventoryTitle),
                InventoryLine.valueOf(InventoryConfiguration.get(InventoryConfiguration::categoryInventoryLines)));
        this.marketDAO = marketDAO;
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
                        .name("§aVoltar")
                        .flag(ItemFlag.values())
                        .build())
                .addDefaultCallback(click -> viewer.openBackInventory()));

    }

    @Override
    protected void onUpdate(PagedViewer viewer, InventoryEditor editor) {

    }

    @Override
    public List<InventoryItem> getPagesItems(PagedViewer viewer) {

        List<InventoryItem> items = new LinkedList<>();

        Category category = categoryManager.getCategoryMap().get(viewer.getProperty("category").toString());

        for (MarketItem marketItem : marketDAO.findAllMarketItemList()) {

            assert category != null;
            if (category.getAllowedMaterials().contains(marketItem.getItemStack().getType())) {

                ItemStack itemStack = marketItem.getItemStack();
                items.add(new InventoryItem(itemStack));

            }

        }
        return items;
    }
}
