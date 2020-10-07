package com.nextplugin.nextmarket.api;

import com.nextplugin.nextmarket.NextMarket;
import com.nextplugin.nextmarket.api.category.Category;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Market {

    private final List<Category> categories;

    public Market(NextMarket plugin) {
        this.categories = new ArrayList<>();
    }

    public List<MarketItem> getMarketItemsFrom(Player player) {
        List<MarketItem> items = new ArrayList<>();

        for (Category category : getCategories()) {
            items.addAll(category
                    .getItems()
                    .stream()
                    .filter(marketItem ->
                            marketItem
                            .getPlayer()
                            .getName()
                            .equals(player.getName()))
                    .collect(Collectors.toList()));
        }

        return items;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Category getCategoryToItem(ItemStack itemStack) {
        Material material = itemStack.getType();

        for (Category category : getCategories()) {
            if (category.getValidTypes().contains(material)) return category;
        }

        return null;
    }

}
