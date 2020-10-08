package com.nextplugin.nextmarket.sql;

import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.sql.provider.ConnectionBuilder;
import com.nextplugin.nextmarket.sql.provider.DatabaseProvider;
import com.nextplugin.nextmarket.sql.provider.document.impl.MarketItemSerializer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MarketDatabaseAccessObject extends DatabaseProvider {

    public MarketDatabaseAccessObject(ConnectionBuilder connectionBuilder) {
        super(connectionBuilder);
    }

    public void create() {
        update("create table if not exists `market_items` (" +
                "`owner` char(36) not null, " +
                "`market_item` text not null, " +
                "`destination` char(36)," +
                "`created_at` integer(8) not null" +
                ");");
    }

    public void shutdown() {
        super.shutdown();
    }

    public List<MarketItem> getAllMarketItems() {
        return queryMany("select `market_item` from `market_items`")
                .stream()
                .map(document -> document.get(new MarketItemSerializer(), "market_item"))
                .collect(Collectors.toList());
    }

    public List<MarketItem> getAllExpiredItems() {
        return getAllMarketItems()
                .stream()
                .filter(item -> item.isExpired(3600))
                .collect(Collectors.toList());
    }

    public List<MarketItem> getMarketItemsFrom(Player player) {
        String id = player.getUniqueId().toString();

        return queryMany("select `market_item` from `market_items` where `owner` = ?", id)
                .stream()
                .map(document -> document.get(new MarketItemSerializer(), "market_item"))
                .collect(Collectors.toList());
    }

    public List<MarketItem> getExpiredItemsFrom(Player player) {
        return getMarketItemsFrom(player)
                .stream()
                .filter(item -> item.isExpired(3600))
                .collect(Collectors.toList());
    }

    public void saveItem(MarketItem item) {
        update("insert into `market_items` values (?, ?, ?, ?);",
                item.getSeller().getUniqueId(),
                new MarketItemSerializer().serialize(item),
                item.getDestination().getUniqueId(),
                System.currentTimeMillis());
    }

    public void removeItem(MarketItem item) {
        update("delete from `market_items` where `market_item` = ? and `owner` = ?",
                new MarketItemSerializer().serialize(item),
                item.getSeller().getUniqueId());
    }

}
