package com.nextplugins.nextmarket.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.ConfigValue;
import com.nextplugins.nextmarket.configuration.value.MessageValue;
import com.nextplugins.nextmarket.storage.ProductStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Singleton
public final class ProductManager {

    @Inject private CategoryManager categoryManager;
    @Inject private ProductStorage productStorage;

    public void init() {
        productStorage.init();
    }

    public Product createProduct(Player player, String destinationName, double price) {
        if (player.getName().equalsIgnoreCase(destinationName)) {
            player.sendMessage(MessageValue.get(MessageValue::sellingForYou));
            return null;
        }

        OfflinePlayer destination = null;
        if (destinationName != null) {
            destination = Bukkit.getPlayer(destinationName);
            if (destination == null) {
                player.sendMessage(MessageValue.get(MessageValue::offlinePlayerMessage));
                return null;
            }
        }

        double maxValue = ConfigValue.get(ConfigValue::maximumAnnouncementValue);
        double minValue = ConfigValue.get(ConfigValue::minimumAnnouncementValue);

        if (maxValue != -1 && price > maxValue) {
            player.sendMessage(MessageValue.get(MessageValue::maximumValueReachedMessage));
            return null;
        } else if (price < minValue) {
            player.sendMessage(MessageValue.get(MessageValue::minimumValueNotReachedMessage));
            return null;
        }

        ItemStack itemStack = player.getItemInHand();
        Category category = categoryManager.findCategoryByMaterial(itemStack.getType()).orElse(null);
        if (category == null) {
            player.sendMessage(MessageValue.get(MessageValue::invalidItemMessage));
            return null;
        }

        int playerTotalProducts = productStorage.findProductsByPlayer(player).size();
        int playerLimit = getPlayerLimit(player);
        if (playerTotalProducts >= playerLimit) {
            player.sendMessage(MessageValue.get(MessageValue::outOfBoundsMessage)
                    .replace("%limit%", String.valueOf(playerLimit))
            );
            return null;
        }

        return Product.builder()
                .seller(player)
                .destination(destination)
                .price(price)
                .itemStack(itemStack)
                .category(category)
                .build();
    }

    public void insertProductCategory(Product product) {
        categoryManager.findCategoryByMaterial(product.getItemStack().getType()).ifPresent(product::setCategory);
    }

    public int getPlayerLimit(Player player) {
        int playerLimit = 0;

        ConfigurationSection limitSection = ConfigValue.get(ConfigValue::sellLimit);
        for (String limitPermission : limitSection.getKeys(false)) {
            if (player.hasPermission(limitPermission)) {
                playerLimit = limitSection.getInt(limitPermission);
            }
        }

        return playerLimit;
    }

}
