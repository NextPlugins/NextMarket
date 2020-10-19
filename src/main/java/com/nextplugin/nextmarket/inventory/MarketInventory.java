package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.global.GlobalInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.IViewer;
import com.nextplugin.nextmarket.api.category.icon.CategoryIcon;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.configuration.InventoryConfiguration;
import com.nextplugin.nextmarket.manager.CategoryManager;
import com.nextplugin.nextmarket.sql.MarketDAO;
import com.nextplugin.nextmarket.util.ItemBuilder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class MarketInventory extends GlobalInventory {

    private final CategoryManager categoryManager;
    private final MarketDAO marketDAO;

    public MarketInventory(CategoryManager categoryManager, MarketDAO marketDAO) {

        super("nextmarket.market",
                InventoryConfiguration.get(InventoryConfiguration::mainInventoryTitle),
                InventoryLine.valueOf(InventoryConfiguration.get(InventoryConfiguration::mainInventoryLines))
        );

        this.categoryManager = categoryManager;
        this.marketDAO = marketDAO;
    }

    @Override
    protected void onCreate(InventoryEditor editor) {

        categoryManager.getCategoryMap().forEach((s, category) -> {

            CategoryIcon icon = category.getIcon();

            List<String> lore = new ArrayList<>();

            for (String string : category.getDescription()) {
                lore.add(string.replace("&", "§"));
            }

            List<MarketItem> collect = marketDAO.findAllMarketItemList()
                    .stream()
                    .filter(marketItem -> category.getAllowedMaterials().contains(marketItem.getItemStack().getType()))
                    .collect(Collectors.toList());

            ItemStack itemStack = new ItemBuilder(icon.getItemStack().getType())
                    .amount(1)
                    .durability(icon.getItemStack().getDurability())
                    .name(category.getDisplayName()
                            .replace("&", "§")
                            .replace("%amount%", collect.size() + ""))
                    .lore(lore)
                    .flag(ItemFlag.values())
                    .build();

            editor.setItem(icon.getPosition(), new InventoryItem(itemStack).addDefaultCallback(click -> {

                CategoryInventory categoryInventory = new CategoryInventory(marketDAO, categoryManager);

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
