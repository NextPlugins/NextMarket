package com.nextplugins.nextmarket.listener;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.api.event.ProductCreateEvent;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.MessageValue;
import com.nextplugins.nextmarket.compat.PlayerHands;
import com.nextplugins.nextmarket.manager.AnnouncementManager;
import com.nextplugins.nextmarket.storage.ProductStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class ProductCreateListener implements Listener {

    @Inject private AnnouncementManager announcementManager;
    @Inject private ProductStorage productStorage;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onProductCreate(ProductCreateEvent event) {
        Player player = event.getPlayer();
        Product product = event.getProduct();

        ItemStack hand = PlayerHands.getMainHand(player);
        ItemStack expected = product.getItemStack();
        if (hand == null
                || hand.getType() != expected.getType()
                || hand.getAmount() != expected.getAmount()
                || !hand.isSimilar(expected)) {
            event.setCancelled(true);
            player.sendMessage(MessageValue.get(MessageValue::changedHandItemMessage));
            return;
        }

        PlayerHands.clearMainHand(player);
        productStorage.insertOne(product);

        if (product.getDestination() == null) {
            announcementManager.sendCreationAnnounce(
                    event,
                    MessageValue.get(MessageValue::sellingAItemMessage),
                    MessageValue.get(MessageValue::announcementMessage),
                    true,
                    target -> !target.equals(player)
            );
        } else {
            announcementManager.sendCreationAnnounce(
                    event,
                    MessageValue.get(MessageValue::sellingAItemInPersonalMarket),
                    MessageValue.get(MessageValue::privateAnnouncementMessage),
                    false,
                    target -> Objects.equals(target, product.getDestination())
            );
        }
    }

}
