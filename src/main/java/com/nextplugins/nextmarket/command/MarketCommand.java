package com.nextplugins.nextmarket.command;

import com.google.inject.Inject;
import com.henryfabio.minecraft.inventoryapi.viewer.property.ViewerPropertyMap;
import com.nextplugins.nextmarket.api.event.ProductCreateEvent;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.MessageValue;
import com.nextplugins.nextmarket.inventory.CategoryInventory;
import com.nextplugins.nextmarket.inventory.ConfirmationInventory;
import com.nextplugins.nextmarket.inventory.MarketInventory;
import com.nextplugins.nextmarket.manager.CategoryManager;
import com.nextplugins.nextmarket.manager.ProductManager;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.storage.ProductStorage;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Henry Fábio
 */
public final class MarketCommand {

    @Inject private CategoryManager categoryManager;

    @Inject private ProductManager productManager;
    @Inject private ProductStorage productStorage;

    @Inject private InventoryRegistry inventoryRegistry;

    @Command(
            name = "market",
            aliases = {"mercado"},
            permission = "nextmarket.use",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void marketCommand(Context<Player> context) {
        context.sendMessage(MessageValue.get(MessageValue::commandMessage).toArray(new String[]{}));
    }

    @Command(
            name = "market.show",
            aliases = {"ver"},
            async = true
    )
    public void showMarketCommand(Context<Player> context, @Optional String id) {
        if (id == null) {
            inventoryRegistry.getMarketInventory().openInventory(context.getSender());
        } else {
            Category category = categoryManager.findCategoryById(id).orElse(null);
            if (category == null) {
                String message = MessageValue.get(MessageValue::categoryNotExists);
                context.sendMessage(message);
            } else {
                inventoryRegistry.getCategoryInventory().openInventory(context.getSender(), viewer -> {
                    ViewerPropertyMap propertyMap = viewer.getPropertyMap();
                    propertyMap.set("category", category);
                    propertyMap.set("products", productStorage.findProductsByCategory(category));
                });
            }
        }
    }

    @Command(
            name = "market.personal",
            aliases = {"pessoal"},
            async = true
    )
    public void personalMarketCommand(Context<Player> context) {

    }

    @Command(
            name = "market.sell",
            aliases = {"vender"},
            async = true
    )
    public void sellMarketCommand(Context<Player> context, double price, @Optional String destination) {
        Product product = productManager.createProduct(context.getSender(), destination, price);
        if (product == null) return;

        inventoryRegistry.getConfirmationInventory().openConfirmation(context.getSender(), "Venda de item", () -> {
            ProductCreateEvent createEvent = new ProductCreateEvent(context.getSender(), product);
            Bukkit.getPluginManager().callEvent(createEvent);
        }, product.getItemStack());
    }

    @Command(
            name = "market.announced",
            aliases = {"anunciados"},
            async = true
    )
    public void announcedMarketCommand(Context<Player> context) {

    }

}
