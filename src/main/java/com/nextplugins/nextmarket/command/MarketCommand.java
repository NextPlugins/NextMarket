package com.nextplugins.nextmarket.command;

import com.google.inject.Inject;
import com.henryfabio.minecraft.inventoryapi.viewer.property.ViewerPropertyMap;
import com.nextplugins.nextmarket.api.event.ProductCreateEvent;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.MessageValue;
import com.nextplugins.nextmarket.inventory.PersonalMarketInventory;
import com.nextplugins.nextmarket.inventory.SellingMarketInventory;
import com.nextplugins.nextmarket.manager.CategoryManager;
import com.nextplugins.nextmarket.manager.ProductManager;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.storage.ProductStorage;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class MarketCommand {

    @Inject private CategoryManager categoryManager;

    @Inject private ProductManager productManager;
    @Inject private ProductStorage productStorage;

    @Inject private InventoryRegistry inventoryRegistry;

    @Command(
            name = "mercado",
            aliases = {"market"},
            permission = "nextmarket.use",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void marketCommand(Context<Player> context) {
        context.sendMessage(MessageValue.get(MessageValue::commandMessage).toArray(new String[]{}));
    }

    @Command(
            name = "mercado.ver",
            aliases = {"show"},
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
            name = "mercado.pessoal",
            aliases = {"personal"},
            async = true
    )
    public void personalMarketCommand(Context<Player> context) {
        try {
            PersonalMarketInventory personalMarketInventory = inventoryRegistry.getPersonalMarketInventory();
            personalMarketInventory.openInventory(context.getSender());
        } catch (Throwable ignored) {
            context.getSender().closeInventory();
            context.getSender().sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
        }
    }

    @Command(
            name = "mercado.vender",
            aliases = {"sell"},
            async = true
    )
    public void sellMarketCommand(Context<Player> context, @Optional String priceText, @Optional String destination) {
        if (priceText == null) {

            context.sendMessage(MessageValue.get(MessageValue::correctUsageSellMessage));
            return;

        }

        double price;

        try {
            price = Double.parseDouble(priceText);
        } catch (Throwable ignored) {
            context.getSender().sendMessage(MessageValue.get(MessageValue::invalidNumber));
            return;
        }

        if (Double.isNaN(price) || Double.isInfinite(price)) {

            context.getSender().sendMessage(MessageValue.get(MessageValue::invalidNumber));
            return;

        }

        Product product = productManager.createProduct(context.getSender(), destination, price);
        if (product == null) return;

        inventoryRegistry.getConfirmationInventory().openConfirmation(context.getSender(), "Venda de item", () -> {
            ProductCreateEvent createEvent = new ProductCreateEvent(context.getSender(), product);
            Bukkit.getPluginManager().callEvent(createEvent);
        }, product.getItemStack());
    }

    @Command(
            name = "mercado.anunciados",
            aliases = {"selling", "vendidos"},
            async = true
    )
    public void sellingMarketCommand(Context<Player> context) {
        try {
            SellingMarketInventory sellingMarketInventory = inventoryRegistry.getSellingMarketInventory();
            sellingMarketInventory.openInventory(context.getSender());
        } catch (Throwable ignored) {
            context.getSender().closeInventory();
            context.getSender().sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
        }
    }

}
