package com.nextplugins.nextmarket.registry;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.inventory.*;
import lombok.Getter;

@Getter
@Singleton
public final class InventoryRegistry {

    private MarketInventory marketInventory;
    private CategoryInventory categoryInventory;
    private ConfirmationInventory confirmationInventory;
    private PersonalMarketInventory personalMarketInventory;
    private SellingMarketInventory sellingMarketInventory;

    public void init() {
        this.marketInventory = new MarketInventory().init();
        this.categoryInventory = new CategoryInventory().init();
        this.confirmationInventory = new ConfirmationInventory().init();
        this.personalMarketInventory = new PersonalMarketInventory().init();
        this.sellingMarketInventory = new SellingMarketInventory().init();
    }

}
