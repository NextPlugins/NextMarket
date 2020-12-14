package com.nextplugins.nextmarket.listener;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.api.event.ProductRemoveEvent;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.MessageValue;
import com.nextplugins.nextmarket.storage.ProductStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;

public final class ProductRemoveListener implements Listener {

    @Inject private ProductStorage productStorage;

    @EventHandler
    private void onProductRemove(ProductRemoveEvent event) {
        Player player = event.getPlayer();

        Product product = event.getProduct();
        if (!product.isAvailable()) {
            event.setCancelled(true);
            player.sendMessage(MessageValue.get(MessageValue::unavailableProduct));
            return;
        }

        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() == -1) {
            event.setCancelled(true);
            player.sendMessage(MessageValue.get(MessageValue::fullInventoryMessage));
            return;
        }

        product.setAvailable(false);
        productStorage.deleteOne(product);

        inventory.addItem(product.getItemStack());

        player.sendMessage(MessageValue.get(MessageValue::cancelAnSellMessage));
    }

}
