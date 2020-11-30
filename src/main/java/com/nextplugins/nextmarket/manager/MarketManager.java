package com.nextplugins.nextmarket.manager;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.inventory.MarketInventory;
import lombok.Getter;

/**
 * @author Henry Fábio
 */
@Getter
@Singleton
public final class MarketManager {

    private MarketInventory marketInventory;

    public void init() {
        this.marketInventory = new MarketInventory().init();
    }

}
