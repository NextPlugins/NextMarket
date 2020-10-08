package com.nextplugin.nextmarket.api.market;

import com.nextplugin.nextmarket.NextMarket;
import com.nextplugin.nextmarket.api.MarketItem;
import com.nextplugin.nextmarket.api.category.Category;
import com.nextplugin.nextmarket.api.category.loader.CategoryLoader;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface Market {

    List<Category> categories = new CategoryLoader(NextMarket.getInstance()).load();

    default List<MarketItem> getMarketItemsFrom(Player player) {
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

    default List<Category> getCategories() {
        return categories;
    }

    default Category getCategoryToItem(ItemStack itemStack) {
        Material material = itemStack.getType();

        for (Category category : getCategories()) {
            if (category.getValidTypes().contains(material)) return category;
        }

        return null;
    }

}
