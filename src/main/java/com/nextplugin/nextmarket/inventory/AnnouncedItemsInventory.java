package com.nextplugin.nextmarket.inventory;

import com.google.inject.Inject;
import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.paged.PagedInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.paged.PagedViewer;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.configuration.ConfigValue;
import com.nextplugin.nextmarket.configuration.InventoryConfiguration;
import com.nextplugin.nextmarket.sql.MarketDAO;
import com.nextplugin.nextmarket.util.ItemBuilder;
import com.nextplugin.nextmarket.util.NumberUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class AnnouncedItemsInventory extends PagedInventory {

    @Inject
    private MarketDAO marketDAO;
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
                        .name("§cVoltar")
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

        for (MarketItem marketItem : this.marketCache.getMarketCache()) {
            if (!marketItem.getSellerId().equals(viewer.getPlayer().getUniqueId())) continue;

            ItemStack itemStack = marketItem.getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> lore = itemMeta.getLore();
            lore.add("");
            lore.add("§7Valor: §a$" + NumberUtil.letterFormat(marketItem.getPrice()));

            if (marketItem.getDestinationId() != null) {

                lore.add("§7Destinatário: §a" + marketItem.getDestination().getName());

            }

            lore.add("§7Clique para remover do mercado");

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            InventoryItem inventoryItem = new InventoryItem(itemStack);
            inventoryItem.addDefaultCallback(click -> {

                PlayerInventory inventory = click.getPlayer().getInventory();
                if (inventory.firstEmpty() == -1) {
                    click.getPlayer().sendMessage(ConfigValue.get(ConfigValue::fullInventoryMessage));
                    return;
                }

                this.marketCache.removeItem(marketItem);
                marketDAO.deleteMarketItem(marketItem);

                inventory.addItem(marketItem.getItemStack());
                click.getPlayer().sendMessage(ConfigValue.get(ConfigValue::cancellAnSellMessage));

            });

            items.add(inventoryItem);

        }

        return items;
    }

}
