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
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.InventoryValue;
import com.nextplugins.nextmarket.inventory.button.InventoryButton;
import com.nextplugins.nextmarket.registry.InventoryButtonRegistry;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.storage.ProductStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class PersonalMarketInventory extends PagedInventory {

    @Inject private ProductStorage productStorage;
    @Inject private InventoryRegistry inventoryRegistry;
    @Inject private InventoryButtonRegistry inventoryButtonRegistry;

    public PersonalMarketInventory() {
        super(
                "market.personal",
                InventoryValue.get(InventoryValue::privateInventoryTitle),
                InventoryValue.get(InventoryValue::privateInventoryLines) * 9
        );

        NextMarket.getInstance().getInjector().injectMembers(this);
        configuration(configuration -> configuration.secondUpdate(5));
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        ViewerConfigurationImpl.Paged configuration = viewer.getConfiguration();
        configuration.backInventory("market.main");
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
            if (product.isExpired()) continue;
            itemSuppliers.add(() -> productInventoryItem(product));
        }
        return itemSuppliers;
    }

    private InventoryItem productInventoryItem(Product product) {
        return InventoryItem.of(product.toViewItemStack(InventoryValue.get(InventoryValue::privateInventoryItemLore)))
                .defaultCallback(event -> {
                    Player player = event.getPlayer();
                    inventoryRegistry.getConfirmationInventory().openConfirmation(player,
                            "Comprar item",
                            () -> {
                                ProductBuyEvent buyEvent = new ProductBuyEvent(player, product);
                                Bukkit.getPluginManager().callEvent(buyEvent);
                                event.updateInventory();
                            });
                });
    }

    private InventoryItem updateInventoryItem(Viewer viewer) {
        InventoryButton inventoryButton = inventoryButtonRegistry.get("personal.update");
        ItemStack itemStack = inventoryButton.getSkullItemStack(viewer.getName());

        return InventoryItem.of(itemStack)
                .defaultCallback(CustomInventoryClickEvent::updateInventory);
    }

    private void updateCategoryItems(Viewer viewer) {
        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
        propertyMap.set("products", productStorage.findProductsByDestination(viewer.getPlayer()));
    }

}
