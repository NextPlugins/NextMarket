package com.nextplugin.nextmarket.cache;

import com.google.inject.Singleton;
import com.nextplugin.nextmarket.api.item.MarketItem;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@Singleton
public class MarketCache {

    private List<MarketItem> marketCache = new LinkedList<>();

    public void addItem(MarketItem marketItem) {
        this.marketCache.add(marketItem);
    }

    public void removeItem(MarketItem marketItem) {
        this.marketCache.remove(marketItem);
    }

}
