package com.nextplugins.nextmarket.inventory;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.event.ProductRemoveEvent;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SellingMarketInventory {

    @Inject private ProductStorage productStorage;
    @Inject private InventoryRegistry inventoryRegistry;
    @Inject private InventoryButtonRegistry inventoryButtonRegistry;

    public SellingMarketInventory() {
        NextMarket.getInstance().getInjector().injectMembers(this);
    }

    public void openInventory(Player player) {
        String title = InventoryValue.get(InventoryValue::sellingInventoryTitle);
        int size = InventoryValue.get(InventoryValue::sellingInventoryLines) * 9;

        new PagedMarketMenu(title, size) {
            @Override
            protected void collectItems(Player viewer, List<PageItem> items) {
                Set<Product> products = productStorage.findProductsBySeller(viewer);
                for (Product product : new ArrayList<Product>(products)) {
                    if (product == null || !product.isAvailable()) continue;

                    ItemStack view = product.toViewItemStack(
                            InventoryValue.get(InventoryValue::sellingInventoryItemLore)
                    );
                    if (product.isExpired()) {
                        addExpiredTag(view);
                    }

                    items.add(new PageItem(view, event -> {
                        Player clicker = (Player) event.getWhoClicked();
                        inventoryRegistry.getConfirmationInventory().openConfirmation(
                                clicker,
                                "Recolher item",
                                () -> {
                                    ProductRemoveEvent removeEvent = new ProductRemoveEvent(clicker, product);
                                    Bukkit.getPluginManager().callEvent(removeEvent);
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
                InventoryButton inventoryButton = inventoryButtonRegistry.get("selling.update");
                if (inventoryButton == null) {
                    return super.createUpdateItem(viewer);
                }
                return inventoryButton.getSkullItemStack(viewer.getName());
            }
        }.open(player);
    }

    private void addExpiredTag(ItemStack itemStack) {
        String expiredTag = InventoryValue.get(InventoryValue::sellingExpiredTag);
        if (expiredTag == null || expiredTag.isEmpty()) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<String>();
        } else {
            lore = new ArrayList<String>(lore);
        }
        lore.add(expiredTag);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
    }

}
