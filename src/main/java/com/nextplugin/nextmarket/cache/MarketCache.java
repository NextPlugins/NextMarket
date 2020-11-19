package com.nextplugin.nextmarket.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.api.item.QueueItemOperation;
import com.nextplugin.nextmarket.api.item.QueuedMarketItem;
import com.nextplugin.nextmarket.sql.MarketDAO;
import com.nextplugin.nextmarket.task.MarketTransferQueue;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@Singleton
public class MarketCache {

    @Inject MarketDAO marketDAO;
    private final MarketTransferQueue marketTransferQueue = new MarketTransferQueue(marketDAO);
    private List<MarketItem> cache = new LinkedList<>();

    /**
     * Add item to cache
     *
     * @param marketItem the item to add in queue
     * @param newItem    whether the item already exists or is new
     */
    public void addItem(MarketItem marketItem, boolean newItem) {
        this.cache.add(marketItem);

        if (newItem) {

            QueuedMarketItem item = new QueuedMarketItem(marketItem, QueueItemOperation.INSERT);
            marketTransferQueue.addItem(item);

        }
    }

    /**
     * Remove item from system (cache and database)
     *
     * @param marketItem the item to remove
     */
    public void removeItem(MarketItem marketItem) {
        this.cache.remove(marketItem);

        QueuedMarketItem item = new QueuedMarketItem(marketItem, QueueItemOperation.DELETE);
        marketTransferQueue.addItem(item);
    }

}
