package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.paged.PagedInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.paged.PagedViewer;

import java.util.List;

public class AnnouncedItemsInventory extends PagedInventory {

    public AnnouncedItemsInventory() {
        super("nextmarket.announced", "", InventoryLine.FIVE);
    }

    @Override
    protected void onCreate(PagedViewer viewer) {
        super.onCreate(viewer);
    }

    @Override
    protected void onOpen(PagedViewer viewer, InventoryEditor editor) {

    }

    @Override
    protected void onUpdate(PagedViewer viewer, InventoryEditor editor) {

    }

    @Override
    public List<InventoryItem> getPagesItems(PagedViewer viewer) {
        return null;
    }

}
