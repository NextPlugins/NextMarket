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
import com.nextplugins.nextmarket.api.model.category.CategoryIcon;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.InventoryValue;
import com.nextplugins.nextmarket.inventory.button.InventoryButton;
import com.nextplugins.nextmarket.registry.InventoryButtonRegistry;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.storage.ProductStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class CategoryInventory extends PagedInventory {

    @Inject
    private ProductStorage productStorage;
    @Inject
    private InventoryRegistry inventoryRegistry;
    @Inject
    private InventoryButtonRegistry inventoryButtonRegistry;

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

        configuration.nextPageSlot(53);
        configuration.previousPageSlot(45);
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
        return InventoryItem.of(product.toViewItemStack((
                product.getSeller().getName().equalsIgnoreCase(viewer.getName()) ?
                        InventoryValue.get(InventoryValue::sellingInventoryItemLore) :
                        InventoryValue.get(InventoryValue::categoryInventoryItemLore)))
        ).defaultCallback(event -> {
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

    private InventoryItem updateInventoryItem(Viewer viewer) {
        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
        Category category = propertyMap.get("category");

        InventoryButton inventoryButton = inventoryButtonRegistry.get("category.update");
        ItemStack itemStack = inventoryButton.getItemStack().clone();

        if (itemStack.getType() == Material.BARRIER) {
            CategoryIcon categoryIcon = category.getIcon();

            MaterialData materialData = categoryIcon.getMaterialData();
            itemStack.setType(materialData.getItemType());
            itemStack.setDurability(materialData.getData());

            if (categoryIcon.isEnchant() && Enchantment.DURABILITY.canEnchantItem(itemStack)) {
                itemStack.addEnchantment(Enchantment.DURABILITY, 1);
            }
        }

        return InventoryItem.of(itemStack)
                .defaultCallback(CustomInventoryClickEvent::updateInventory);
    }

    private void updateCategoryItems(Viewer viewer) {
        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
        propertyMap.set("products", productStorage.findProductsByCategory(propertyMap.get("category")));
    }

}
