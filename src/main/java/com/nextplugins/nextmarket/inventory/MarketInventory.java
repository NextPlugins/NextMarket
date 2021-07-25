package com.nextplugins.nextmarket.inventory;

import com.google.inject.Inject;
import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.property.ViewerPropertyMap;
import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.InventoryValue;
import com.nextplugins.nextmarket.inventory.button.InventoryButton;
import com.nextplugins.nextmarket.manager.CategoryManager;
import com.nextplugins.nextmarket.registry.InventoryButtonRegistry;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.storage.ProductStorage;
import com.nextplugins.nextmarket.util.VersionUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class MarketInventory extends SimpleInventory {

    @Inject private CategoryManager categoryManager;
    @Inject private ProductStorage productStorage;

    @Inject private InventoryRegistry inventoryRegistry;
    @Inject private InventoryButtonRegistry inventoryButtonRegistry;

    public MarketInventory() {
        super(
                "market.main",
                InventoryValue.get(InventoryValue::mainInventoryTitle),
                InventoryValue.get(InventoryValue::mainInventoryLines) * 9
        );

        NextMarket.getInstance().getInjector().injectMembers(this);
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {

        Map<Category, Set<Product>> allProducts = productStorage.getProducts();
        for (Category category : categoryManager.getCategoryMap().values()) {
            Set<Product> products = new LinkedHashSet<>(allProducts.getOrDefault(category, Collections.emptySet()));
            products.removeIf(product -> product.getDestination() != null || product.isExpired());
            editor.setItem(
                    category.getIcon().getInventorySlot(),
                    categoryInventoryItem(category, products)
            );
        }

        InventoryButton personalMarketButton = inventoryButtonRegistry.get("main.personalMarket");
        editor.setItem(personalMarketButton.getInventorySlot(), personalMarketInventoryItem(personalMarketButton));

        InventoryButton sellingMarketButton = inventoryButtonRegistry.get("main.sellingMarket");
        editor.setItem(sellingMarketButton.getInventorySlot(), sellingMarketInventoryItem(sellingMarketButton));
    }

    private InventoryItem categoryInventoryItem(Category category, Set<Product> products) {
        return InventoryItem.of(categoryItemStack(category, products)).defaultCallback(event -> {
            try {
                inventoryRegistry.getCategoryInventory().openInventory(event.getPlayer(), viewer -> {
                    ViewerPropertyMap propertyMap = viewer.getPropertyMap();
                    propertyMap.set("category", category);
                    propertyMap.set("products", products);
                });
            } catch (Throwable ignored) {
                event.getPlayer().closeInventory();
                event.getPlayer().sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
            }
        });
    }

    private ItemStack categoryItemStack(Category category, Set<Product> products) {
        MaterialData materialData = category.getIcon().getMaterialData();
        ItemStack itemStack = new ItemStack(materialData.getItemType(),
                VersionUtils.isLegacy() ? Math.min(products.size(), 64) : 1,
                materialData.getData());

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(category.getDisplayName()
                .replace("%amount%", String.valueOf(products.size()))
        );
        itemMeta.setLore(category.getDescription());
        itemMeta.addItemFlags(ItemFlag.values());
        if (category.getIcon().isEnchant()) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private InventoryItem personalMarketInventoryItem(InventoryButton inventoryButton) {
        return InventoryItem.of(inventoryButton.getItemStack()).defaultCallback(event -> {
            try {
                PersonalMarketInventory personalMarketInventory = inventoryRegistry.getPersonalMarketInventory();
                personalMarketInventory.openInventory(event.getPlayer());
            } catch (Throwable ignored) {
                event.getPlayer().closeInventory();
                event.getPlayer().sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
            }
        });
    }

    private InventoryItem sellingMarketInventoryItem(InventoryButton inventoryButton) {
        return InventoryItem.of(inventoryButton.getItemStack()).defaultCallback(event -> {
            try {
                SellingMarketInventory sellingMarketInventory = inventoryRegistry.getSellingMarketInventory();
                sellingMarketInventory.openInventory(event.getPlayer());
            } catch (Throwable ignored) {
                event.getPlayer().closeInventory();
                event.getPlayer().sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
            }
        });
    }

}
