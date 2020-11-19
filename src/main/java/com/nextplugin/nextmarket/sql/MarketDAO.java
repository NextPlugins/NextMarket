package com.nextplugin.nextmarket.sql;

import com.google.inject.Singleton;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.sql.provider.DatabaseProvider;
import com.nextplugin.nextmarket.sql.provider.document.parser.impl.MarketItemDocumentParser;
import com.nextplugin.nextmarket.util.ItemStackUtil;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class MarketDAO extends DatabaseProvider {

    public void createTable() {
        update("create table if not exists `market_items` ("
                + "`seller` char(36) not null, "
                + "`itemStack` text not null, "
                + "`price` double not null,"
                + "`created_at` integer(8) not null,"
                + "`destination` char(36)"
                + ");");
    }

    public List<MarketItem> findAllMarketItemList() {
        return queryMany("select * from `market_items`")
                .stream()
                .map(document -> document.parse(MarketItemDocumentParser.getInstance()))
                .collect(Collectors.toList());
    }

    public void insertMarketItem(MarketItem marketItem) {
        update("insert into `market_items` values (?, ?, ?, ?, ?);",
                marketItem.getSellerId(),
                ItemStackUtil.serialize(marketItem.getItemStack()),
                marketItem.getPrice(),
                marketItem.getCreateTime(),
                marketItem.getDestinationId());
    }

    public void deleteMarketItem(MarketItem marketItem) {
        update("delete from `market_items` where `seller` = ? and `itemStack` = ?",
                marketItem.getSeller().getUniqueId(),
                ItemStackUtil.serialize(marketItem.getItemStack()));
    }

}
