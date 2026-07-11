package com.nextplugins.nextmarket.registry;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.inventory.CategoryInventory;
import com.nextplugins.nextmarket.inventory.ConfirmationInventory;
import com.nextplugins.nextmarket.inventory.MarketInventory;
import com.nextplugins.nextmarket.inventory.PersonalMarketInventory;
import com.nextplugins.nextmarket.inventory.SellingMarketInventory;
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
        this.marketInventory = new MarketInventory();
        this.categoryInventory = new CategoryInventory();
        this.confirmationInventory = new ConfirmationInventory();
        this.personalMarketInventory = new PersonalMarketInventory();
        this.sellingMarketInventory = new SellingMarketInventory();
    }

}
