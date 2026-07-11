package com.nextplugins.nextmarket.inventory;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.event.ProductBuyEvent;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.InventoryValue;
import com.nextplugins.nextmarket.inventory.button.InventoryButton;
import com.nextplugins.nextmarket.inventory.menu.PagedMarketMenu;
import com.nextplugins.nextmarket.registry.InventoryButtonRegistry;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.storage.ProductStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PersonalMarketInventory {

    @Inject private ProductStorage productStorage;
    @Inject private InventoryRegistry inventoryRegistry;
    @Inject private InventoryButtonRegistry inventoryButtonRegistry;

    public PersonalMarketInventory() {
        NextMarket.getInstance().getInjector().injectMembers(this);
    }

    public void openInventory(Player player) {
        String title = InventoryValue.get(InventoryValue::privateInventoryTitle);
        int size = InventoryValue.get(InventoryValue::privateInventoryLines) * 9;

        new PagedMarketMenu(title, size) {
            @Override
            protected void collectItems(Player viewer, List<PageItem> items) {
                Set<Product> products = productStorage.findProductsByDestination(viewer);
                for (Product product : new ArrayList<Product>(products)) {
                    if (product == null || product.isExpired() || !product.isAvailable()) continue;

                    ItemStack view = product.toViewItemStack(
                            InventoryValue.get(InventoryValue::privateInventoryItemLore)
                    );

                    items.add(new PageItem(view, event -> {
                        Player clicker = (Player) event.getWhoClicked();
                        inventoryRegistry.getConfirmationInventory().openConfirmation(
                                clicker,
                                "Comprar item",
                                () -> {
                                    ProductBuyEvent buyEvent = new ProductBuyEvent(clicker, product);
                                    Bukkit.getPluginManager().callEvent(buyEvent);
                                    refresh();
                                }
                        );
                    }));
                }
            }

            @Override
            protected void onBack(InventoryClickEvent event) {
                inventoryRegistry.getMarketInventory().openInventory((Player) event.getWhoClicked());
            }

            @Override
            protected ItemStack createUpdateItem(Player viewer) {
                InventoryButton inventoryButton = inventoryButtonRegistry.get("personal.update");
                if (inventoryButton == null) {
                    return super.createUpdateItem(viewer);
                }
                return inventoryButton.getSkullItemStack(viewer.getName());
            }
        }.open(player);
    }

}
