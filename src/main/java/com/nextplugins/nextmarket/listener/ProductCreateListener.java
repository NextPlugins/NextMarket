package com.nextplugins.nextmarket.listener;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.api.event.ProductCreateEvent;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.MessageValue;
import com.nextplugins.nextmarket.manager.AnnouncementManager;
import com.nextplugins.nextmarket.storage.ProductStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Objects;

public final class ProductCreateListener implements Listener {

    @Inject private AnnouncementManager announcementManager;
    @Inject private ProductStorage productStorage;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onProductCreate(ProductCreateEvent event) {
        Player player = event.getPlayer();
        Product product = event.getProduct();

        if (!Objects.equals(player.getItemInHand(), product.getItemStack())) {
            event.setCancelled(true);
            player.sendMessage(MessageValue.get(MessageValue::changedHandItemMessage));
            return;
        }

        player.setItemInHand(null);
        productStorage.insertOne(product);

        if (product.getDestination() == null) {
            announcementManager.sendCreationAnnounce(
                    event,
                    MessageValue.get(MessageValue::announcedAItemMessage),
                    MessageValue.get(MessageValue::announcementMessage),
                    true,
                    target -> !target.equals(player)
            );
        } else {
            announcementManager.sendCreationAnnounce(
                    event,
                    MessageValue.get(MessageValue::announcedAItemInPersonalMarket),
                    MessageValue.get(MessageValue::privateAnnouncementMessage),
                    false,
                    target -> Objects.equals(target, product.getDestination())
            );
        }
    }

}
