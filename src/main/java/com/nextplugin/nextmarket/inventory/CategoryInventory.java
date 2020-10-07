package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.paged.PagedInventory;
import com.henryfabio.inventoryapi.inventory.single.SingleInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.paged.PagedViewer;
import com.henryfabio.inventoryapi.viewer.single.SingleViewer;

import java.util.List;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class CategoryInventory extends PagedInventory {

    public CategoryInventory() {
        super("nextmarket.category", "", InventoryLine.valueOf(1));
        // TODO: adicionar parâmetros configuráveis (tamanho do inventário)
    }

    @Override
    protected void onCreate(PagedViewer viewer) {
        /*
        Category category = viewer.getProperty("category");
        viewer.setInventoryTitle("§8Categoria: " + category.getName());
         */

        // TODO: implementar sistema de mudança de título de acordo com a categoria
        viewer.setBackInventory("nextmarket.market");
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
