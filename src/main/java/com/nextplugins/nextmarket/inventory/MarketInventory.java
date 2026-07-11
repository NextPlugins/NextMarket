package com.nextplugins.nextmarket.inventory;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.compat.Items;
import com.nextplugins.nextmarket.compat.ServerVersion;
import com.nextplugins.nextmarket.configuration.value.InventoryValue;
import com.nextplugins.nextmarket.inventory.button.InventoryButton;
import com.nextplugins.nextmarket.inventory.menu.MarketMenu;
import com.nextplugins.nextmarket.manager.CategoryManager;
import com.nextplugins.nextmarket.registry.InventoryButtonRegistry;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.storage.ProductStorage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class MarketInventory {

    @Inject private CategoryManager categoryManager;
    @Inject private ProductStorage productStorage;
    @Inject private InventoryRegistry inventoryRegistry;
    @Inject private InventoryButtonRegistry inventoryButtonRegistry;

    public MarketInventory() {
        NextMarket.getInstance().getInjector().injectMembers(this);
    }

    public void openInventory(Player player) {
        String title = InventoryValue.get(InventoryValue::mainInventoryTitle);
        int size = InventoryValue.get(InventoryValue::mainInventoryLines) * 9;

        new MarketMenu(title, size) {
            @Override
            protected void render(Player viewer) {
                Map<Category, Set<Product>> allProducts = productStorage.getProducts();
                for (Category category : categoryManager.getCategoryMap().values()) {
                    Set<Product> products = new LinkedHashSet<Product>(
                            allProducts.getOrDefault(category, Collections.<Product>emptySet())
                    );
                    products.removeIf(product -> product.getDestination() != null || product.isExpired());

                    setItem(
                            category.getIcon().getInventorySlot(),
                            categoryItemStack(category, products),
                            event -> openCategory(event.getWhoClicked() instanceof Player
                                    ? (Player) event.getWhoClicked()
                                    : viewer, category, products)
                    );
                }

                InventoryButton personalMarketButton = inventoryButtonRegistry.get("main.personalMarket");
                if (personalMarketButton != null) {
                    setItem(personalMarketButton.getInventorySlot(), personalMarketButton.getItemStack(), event -> {
                        Player clicker = (Player) event.getWhoClicked();
                        try {
                            inventoryRegistry.getPersonalMarketInventory().openInventory(clicker);
                        } catch (Throwable ignored) {
                            clicker.closeInventory();
                            clicker.sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
                        }
                    });
                }

                InventoryButton sellingMarketButton = inventoryButtonRegistry.get("main.sellingMarket");
                if (sellingMarketButton != null) {
                    setItem(sellingMarketButton.getInventorySlot(), sellingMarketButton.getItemStack(), event -> {
                        Player clicker = (Player) event.getWhoClicked();
                        try {
                            inventoryRegistry.getSellingMarketInventory().openInventory(clicker);
                        } catch (Throwable ignored) {
                            clicker.closeInventory();
                            clicker.sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
                        }
                    });
                }
            }
        }.open(player);
    }

    private void openCategory(Player player, Category category, Set<Product> products) {
        try {
            inventoryRegistry.getCategoryInventory().openInventory(player, category, products);
        } catch (Throwable ignored) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
        }
    }

    private ItemStack categoryItemStack(Category category, Set<Product> products) {
        ItemStack itemStack = category.getIcon().getMaterialData().toItemStack(
                ServerVersion.isLegacy() ? Math.max(1, Math.min(products.size(), 64)) : 1
        );

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(category.getDisplayName()
                    .replace("%amount%", String.valueOf(products.size()))
            );
            itemMeta.setLore(category.getDescription());
            Items.applySafeFlags(itemMeta);
            if (category.getIcon().isEnchant()) {
                org.bukkit.enchantments.Enchantment glow = Items.resolveGlowEnchantment();
                if (glow != null) {
                    itemMeta.addEnchant(glow, 1, true);
                }
            }
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

}
