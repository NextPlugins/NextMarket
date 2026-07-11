package com.nextplugins.nextmarket.inventory;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.event.ProductBuyEvent;
import com.nextplugins.nextmarket.api.event.ProductRemoveEvent;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.compat.Items;
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

public final class CategoryInventory {

    @Inject private ProductStorage productStorage;
    @Inject private InventoryRegistry inventoryRegistry;
    @Inject private InventoryButtonRegistry inventoryButtonRegistry;

    public CategoryInventory() {
        NextMarket.getInstance().getInjector().injectMembers(this);
    }

    public void openInventory(Player player, Category category, Set<Product> products) {
        String title = category.getConfiguration().getInventoryTitle();
        if (title == null || title.isEmpty()) {
            title = InventoryValue.get(InventoryValue::categoryInventoryTitle)
                    .replace("%category_name%", category.getDisplayName());
        }
        int size = InventoryValue.get(InventoryValue::categoryInventoryLines) * 9;

        new PagedMarketMenu(title, size) {
            @Override
            protected void collectItems(Player viewer, List<PageItem> items) {
                Set<Product> current = productStorage.findProductsByCategory(category);
                if (current == null) return;

                for (Product product : new ArrayList<Product>(current)) {
                    if (product == null || !product.isAvailable()) continue;

                    ItemStack view = product.toViewItemStack(
                            product.getSeller().getName() != null
                                    && product.getSeller().getName().equalsIgnoreCase(viewer.getName())
                                    ? InventoryValue.get(InventoryValue::sellingInventoryItemLore)
                                    : InventoryValue.get(InventoryValue::categoryInventoryItemLore)
                    );

                    items.add(new PageItem(view, event -> {
                        Player clicker = (Player) event.getWhoClicked();
                        boolean itemCollect = product.getSeller().getUniqueId().equals(clicker.getUniqueId());

                        inventoryRegistry.getConfirmationInventory().openConfirmation(
                                clicker,
                                itemCollect ? "Recolher item" : "Comprar item",
                                () -> {
                                    if (product.getSeller().getUniqueId().equals(clicker.getUniqueId())) {
                                        ProductRemoveEvent removeEvent = new ProductRemoveEvent(clicker, product);
                                        Bukkit.getPluginManager().callEvent(removeEvent);
                                    } else {
                                        ProductBuyEvent buyEvent = new ProductBuyEvent(clicker, product);
                                        Bukkit.getPluginManager().callEvent(buyEvent);
                                    }
                                    refresh();
                                }
                        );
                    }));
                }
            }

            @Override
            protected void onBack(InventoryClickEvent event) {
                Player clicker = (Player) event.getWhoClicked();
                inventoryRegistry.getMarketInventory().openInventory(clicker);
            }

            @Override
            protected ItemStack createUpdateItem(Player viewer) {
                InventoryButton inventoryButton = inventoryButtonRegistry.get("category.update");
                if (inventoryButton == null) {
                    return super.createUpdateItem(viewer);
                }

                ItemStack itemStack = inventoryButton.getItemStack().clone();
                if (itemStack.getType().name().equals("BARRIER")) {
                    ItemStack icon = category.getIcon().getMaterialData().toItemStack(1);
                    ItemMeta meta = itemStack.getItemMeta();
                    itemStack = icon;
                    if (meta != null) {
                        ItemMeta iconMeta = itemStack.getItemMeta();
                        if (iconMeta != null) {
                            if (meta.hasDisplayName()) iconMeta.setDisplayName(meta.getDisplayName());
                            if (meta.hasLore()) iconMeta.setLore(meta.getLore());
                            if (category.getIcon().isEnchant()) {
                                org.bukkit.enchantments.Enchantment glow = Items.resolveGlowEnchantment();
                                if (glow != null) iconMeta.addEnchant(glow, 1, true);
                            }
                            Items.applySafeFlags(iconMeta);
                            itemStack.setItemMeta(iconMeta);
                        }
                    }
                }
                return itemStack;
            }

        }.open(player);
    }

}
