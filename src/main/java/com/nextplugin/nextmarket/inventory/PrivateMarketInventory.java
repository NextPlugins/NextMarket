package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.paged.PagedInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.paged.PagedViewer;
import com.nextplugin.nextmarket.api.event.MarketItemSellEvent;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.configuration.InventoryConfiguration;
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
public final class PrivateMarketInventory extends PagedInventory {

    private final MarketCache marketCache;

    public PrivateMarketInventory(MarketCache marketCache) {
        super("nextmarket.private_market",
                InventoryConfiguration.get(InventoryConfiguration::privateInventoryTitle),
                InventoryLine.valueOf(InventoryConfiguration.get(InventoryConfiguration::privateInventoryLines)));

        this.marketCache = marketCache;
    }

    @Override
    protected void onCreate(PagedViewer viewer) {
        viewer.setBackInventory("nextmarket.market");
        viewer.setNextPageItemSlot(45);
        viewer.setPreviousPageItemSlot(53);
    }

    @Override
    protected void onOpen(PagedViewer viewer, InventoryEditor editor) {

        editor.setItem(49, new InventoryItem(
                new ItemBuilder(Material.ARROW)
                        .name("&cVoltar")
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

        for (MarketItem marketItem : this.marketCache.getCache()) {
            if (marketItem.getDestinationId() == null || !marketItem.getDestinationId().equals(viewer.getPlayer().getUniqueId())) continue;

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

            InventoryItem inventoryItem = new InventoryItem(itemStack);
            inventoryItem.addDefaultCallback(click -> {

                Bukkit.getServer().getPluginManager().callEvent(new MarketItemSellEvent(click.getPlayer(), marketItem));
                viewer.updatePagesItems();

            });

            items.add(inventoryItem);

        }

        return items;
    }
}
