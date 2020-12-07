package com.nextplugins.nextmarket.registry;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.inventory.CategoryInventory;
import com.nextplugins.nextmarket.inventory.ConfirmationInventory;
import com.nextplugins.nextmarket.inventory.MarketInventory;
import lombok.Getter;

/**
 * @author Henry Fábio
 */
@Getter
@Singleton
public final class InventoryRegistry {

    private MarketInventory marketInventory;
    private CategoryInventory categoryInventory;
    private ConfirmationInventory confirmationInventory;

    public void init() {
        this.marketInventory = new MarketInventory().init();
        this.categoryInventory = new CategoryInventory().init();
        this.confirmationInventory = new ConfirmationInventory().init();
    }

}
