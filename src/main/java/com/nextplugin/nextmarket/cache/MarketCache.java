package com.nextplugin.nextmarket.cache;

import com.google.inject.Singleton;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.api.item.QueueItemOperation;
import com.nextplugin.nextmarket.api.item.QueuedMarketItem;
import com.nextplugin.nextmarket.task.MarketTransferQueue;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@Singleton
public class MarketCache {

    private List<MarketItem> marketCache = new LinkedList<>();

    public void addItem(MarketItem marketItem) {
        this.marketCache.add(marketItem);

        System.out.println("Inserting item " + marketItem.getItemStack().getType().toString() + " from cache and queueing same");
        MarketTransferQueue.getInstance().addItem(new QueuedMarketItem(marketItem, QueueItemOperation.INSERT));
    }

    public void removeItem(MarketItem marketItem) {
        this.marketCache.remove(marketItem);

        System.out.println("Deleting item " + marketItem.getItemStack().getType().toString() + " from cache and queueing same");
        MarketTransferQueue.getInstance().addItem(new QueuedMarketItem(marketItem, QueueItemOperation.DELETE));
    }

}
