package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.single.SingleInventory;
import com.henryfabio.inventoryapi.viewer.single.SingleViewer;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class PrivateMarketInventory extends SingleInventory {

    public PrivateMarketInventory() {
        super("nextmarket.private_market", "§8Mercado Pessoal", InventoryLine.valueOf(5));
        // TODO: adicionar parâmetros configuráveis
    }

    @Override
    protected void onOpen(SingleViewer viewer, InventoryEditor editor) {

    }

    @Override
    protected void onUpdate(SingleViewer viewer, InventoryEditor editor) {

    }

}
