package com.nextplugin.nextmarket.api.category;

import com.nextplugin.nextmarket.api.MarketItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Category {

    private final ItemStack icon;
    private final List<Material> validTypes;
    private final List<MarketItem> items;
    private final int position;

    public Category(ItemStack icon, List<Material> validTypes, List<MarketItem> items, int position) {
        this.icon = icon;
        this.validTypes = validTypes;
        this.items = items;
        this.position = position;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public List<Material> getValidTypes() {
        return validTypes;
    }

    public List<MarketItem> getItems() {
        return items;
    }

    public int getPosition() {
        return position;
    }

}
