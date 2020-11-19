package com.nextplugin.nextmarket.task;

import com.nextplugin.nextmarket.api.item.QueueItemOperation;
import com.nextplugin.nextmarket.api.item.QueuedMarketItem;
import com.nextplugin.nextmarket.cache.CacheQueue;
import com.nextplugin.nextmarket.sql.MarketDAO;

import java.util.concurrent.TimeUnit;

public class MarketTransferQueue extends CacheQueue<QueuedMarketItem> {

    public MarketTransferQueue(MarketDAO marketDAO) {
        super(3, TimeUnit.MINUTES, true);
        startQueue();

        setRemovalAction(item -> {

            if (item.getOperation() == QueueItemOperation.INSERT) marketDAO.insertMarketItem(item.getItem());
            else marketDAO.deleteMarketItem(item.getItem());

        });
    }

}
