package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.paged.PagedInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.paged.PagedViewer;
import com.nextplugin.nextmarket.api.event.MarketItemRemoveEvent;
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

public class AnnouncedItemsInventory extends PagedInventory {

    private final MarketCache marketCache;

    public AnnouncedItemsInventory(MarketCache marketCache) {
        super("nextmarket.announced",
                InventoryConfiguration.get(InventoryConfiguration::announcedInventoryTitle),
                InventoryLine.valueOf(InventoryConfiguration.get(InventoryConfiguration::announcedInventoryLines)));

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
                .addDefaultCallback(click -> {
                    viewer.openBackInventory();
                    viewer.getCustomInventory().updateInventory(click.getPlayer());
                }));

    }

    @Override
    protected void onUpdate(PagedViewer viewer, InventoryEditor editor) {
        // Unused
    }

    @Override
    public List<InventoryItem> getPagesItems(PagedViewer viewer) {

        List<InventoryItem> items = new LinkedList<>();

        for (MarketItem marketItem : this.marketCache.getCache()) {
            if (!marketItem.getSellerId().equals(viewer.getPlayer().getUniqueId())) continue;

            ItemStack itemStack = marketItem.getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> lore;

            lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();

            lore.addAll(InventoryConfiguration.get(InventoryConfiguration::announcedInventoryItemLore)
                    .stream()
                    .map(s -> s.replace("%price%", NumberUtil.formatNumber(marketItem.getPrice())))
                    .collect(Collectors.toList()));

            if (marketItem.getDestinationId() != null) {
                lore.add("");
                lore.add("§fDestinatário:§a " + marketItem.getDestination().getName());
            }

            lore.add("");

            String collectMessage = marketItem.isExpired()
                    ? "§cEste item expirou clique para coleta-lo"
                    : "§aClique para remover do mercado";

            lore.add(collectMessage);

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            InventoryItem inventoryItem = new InventoryItem(itemStack);
            inventoryItem.addDefaultCallback(click -> {
                Bukkit.getPluginManager().callEvent(new MarketItemRemoveEvent(click.getPlayer(), marketItem));
                viewer.updatePagesItems();
            });

            items.add(inventoryItem);

        }

        return items;
    }

}
