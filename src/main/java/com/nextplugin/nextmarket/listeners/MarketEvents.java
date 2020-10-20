package com.nextplugin.nextmarket.listeners;

import com.nextplugin.nextmarket.api.event.MarketItemBuyEvent;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.sql.MarketDAO;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.inject.Inject;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class MarketEvents implements Listener {

    @Inject private MarketDAO marketDAO;
    @Inject private MarketCache marketCache;

    // TODO temporary for tests

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBuyEvent(MarketItemBuyEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        MarketItem marketItem = event.getMarketItem();

        marketDAO.deleteMarketItem(marketItem);
        marketCache.removeItem(marketItem);

        player.getInventory().addItem(marketItem.getItemStack());
    }

}
