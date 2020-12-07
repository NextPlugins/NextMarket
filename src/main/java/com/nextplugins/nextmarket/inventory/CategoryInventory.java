package com.nextplugins.nextmarket.inventory;

import com.google.inject.Inject;
import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.event.impl.CustomInventoryClickEvent;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.enums.DefaultItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.henryfabio.minecraft.inventoryapi.viewer.property.ViewerPropertyMap;
import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.event.ProductBuyEvent;
import com.nextplugins.nextmarket.api.event.ProductRemoveEvent;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.InventoryValue;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.storage.ProductStorage;
import com.nextplugins.nextmarket.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Henry Fábio
 */
public final class CategoryInventory extends PagedInventory {

    @Inject private ProductStorage productStorage;
    @Inject private InventoryRegistry inventoryRegistry;

    public CategoryInventory() {
        super(
                "market.category",
                "",
                InventoryValue.get(InventoryValue::categoryInventoryLines) * 9
        );

        NextMarket.getInstance().getInjector().injectMembers(this);
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
        Category category = propertyMap.get("category");

        ViewerConfigurationImpl.Paged configuration = viewer.getConfiguration();
        configuration.titleInventory(category.getConfiguration().getInventoryTitle());
        configuration.backInventory("market.main");

        configuration.nextPageSlot(45);
        configuration.previousPageSlot(53);
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        editor.setItem(48, DefaultItem.BACK.toInventoryItem(viewer));
        editor.setItem(49, updateInventoryItem(viewer));
    }

    @Override
    protected void update(PagedViewer viewer, InventoryEditor editor) {
        updateCategoryItems(viewer);
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
        Set<Product> products = propertyMap.get("products");

        List<InventoryItemSupplier> itemSuppliers = new LinkedList<>();
        for (Product product : products) {
            itemSuppliers.add(() -> productInventoryItem(viewer, product));
        }
        return itemSuppliers;
    }

    private InventoryItem productInventoryItem(Viewer viewer, Product product) {
        return InventoryItem.of(productItemStack(viewer, product))
                .defaultCallback(event -> {
                    Player player = event.getPlayer();
                    boolean itemCollect = product.getSeller().getUniqueId().equals(player.getUniqueId());

                    inventoryRegistry.getConfirmationInventory().openConfirmation(player,
                            itemCollect ? "Recolher item" : "Comprar item",
                            () -> {
                                if (product.getSeller().getUniqueId().equals(player.getUniqueId())) {
                                    ProductRemoveEvent removeEvent = new ProductRemoveEvent(player, product);
                                    Bukkit.getPluginManager().callEvent(removeEvent);
                                } else {
                                    ProductBuyEvent buyEvent = new ProductBuyEvent(player, product);
                                    Bukkit.getPluginManager().callEvent(buyEvent);
                                }
                                event.updateInventory();
                            });
                });
    }

    private ItemStack productItemStack(Viewer viewer, Product product) {
        ItemStack itemStack = product.getItemStack().clone();

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> itemLore = itemMeta.getLore();
        if (itemLore == null) itemLore = new LinkedList<>();
        itemLore.addAll((product.getSeller().getName().equalsIgnoreCase(viewer.getName()) ?
                         InventoryValue.get(InventoryValue::announcedInventoryItemLore) :
                         InventoryValue.get(InventoryValue::categoryInventoryItemLore))
                .stream()
                .map(lore -> lore
                        .replace("%seller%", product.getSeller().getName())
                        .replace("%price%", NumberUtils.formatNumber(product.getPrice())))
                .collect(Collectors.toList())
        );
        itemMeta.setLore(itemLore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private InventoryItem updateInventoryItem(Viewer viewer) {
        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
        Category category = propertyMap.get("category");

        MaterialData materialData = category.getIcon().getMaterialData();
        ItemStack itemStack = new ItemStack(materialData.getItemType(), 1, materialData.getData());

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack.setItemMeta(itemMeta);

        return InventoryItem.of(itemStack)
                .defaultCallback(CustomInventoryClickEvent::updateInventory);
    }

    private void updateCategoryItems(Viewer viewer) {
        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
        propertyMap.set("products", productStorage.findProductsByCategory(propertyMap.get("category")));
    }

}
