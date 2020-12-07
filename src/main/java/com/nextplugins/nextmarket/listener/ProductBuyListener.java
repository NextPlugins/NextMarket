package com.nextplugins.nextmarket.listener;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.api.event.ProductBuyEvent;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.MessageValue;
import com.nextplugins.nextmarket.hook.EconomyHook;
import com.nextplugins.nextmarket.storage.ProductStorage;
import com.nextplugins.nextmarket.util.NumberUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Henry Fábio
 */
public final class ProductBuyListener implements Listener {

    @Inject private EconomyHook economyHook;
    @Inject private ProductStorage productStorage;

    @EventHandler
    private void onProductBuy(ProductBuyEvent event) {
        Player player = event.getPlayer();

        Product product = event.getProduct();
        if (!product.isAvailable() || product.isExpired()) {
            event.setCancelled(true);
            player.sendMessage(MessageValue.get(MessageValue::unavailableProduct));
            return;
        }

        PlayerInventory inventory = player.getInventory();
        if (player.getInventory().firstEmpty() == -1) {
            event.setCancelled(true);
            player.getPlayer().sendMessage(MessageValue.get(MessageValue::fullInventoryMessage));
            return;
        }

        EconomyResponse economyResponse = economyHook.withdrawCoins(player, product.getPrice());
        if (!economyResponse.transactionSuccess()) {
            event.setCancelled(true);
            player.sendMessage(MessageValue.get(MessageValue::insufficientMoneyMessage)
                    .replace("%difference%", String.valueOf(product.getPrice() - economyResponse.balance)));
            return;
        }

        product.setAvailable(false);
        productStorage.deleteOne(product);

        OfflinePlayer seller = product.getSeller();
        economyHook.depositCoins(seller, product.getPrice());
        inventory.addItem(product.getItemStack());

        player.closeInventory();

        player.sendMessage(MessageValue.get(MessageValue::boughtAnItemMessage));

        if (seller.isOnline()) {
            seller.getPlayer().sendMessage(MessageValue.get(MessageValue::soldAItemMessage)
                    .replace("%amount%", NumberUtils.formatNumber(product.getPrice())));
        }
    }

}
