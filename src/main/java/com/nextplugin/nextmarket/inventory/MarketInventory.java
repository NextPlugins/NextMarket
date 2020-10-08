package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.global.GlobalInventory;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class MarketInventory extends GlobalInventory {

    public MarketInventory() {
        super("nextmarket.market", "§8Mercado", InventoryLine.valueOf(5));
        // TODO: adicionar parâmetros configuráveis
    }

    @Override
    protected void onCreate(InventoryEditor editor) {

    }

    @Override
    protected void onUpdate(InventoryEditor editor) {

    }

}
