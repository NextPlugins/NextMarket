package com.nextplugin.nextmarket.task;

import com.google.inject.Inject;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.sql.MarketDAO;

import java.util.TimerTask;

public class MarketCacheTransferTask extends TimerTask {

    @Inject private MarketCache marketCache;
    @Inject private MarketDAO marketDAO;

    @Override
    public void run() {
        marketCache.getMarketCache().forEach(marketItem -> {
            if(!marketDAO.findAllMarketItemList().contains(marketItem)){
                marketDAO.insertMarketItem(marketItem);
            }
        });
    }

}
