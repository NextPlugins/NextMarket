package com.nextplugin.nextmarket.sql;

import com.nextplugin.nextmarket.sql.provider.ConnectionBuilder;
import com.nextplugin.nextmarket.sql.provider.DatabaseProvider;

public class MarketDatabaseAccessObject extends DatabaseProvider {

    public MarketDatabaseAccessObject(ConnectionBuilder connectionBuilder) {
        super(connectionBuilder);
    }

    public void create() {
        update("create table if not exists `market_items` (" +
                "`owner` char(36) not null, " +
                "`market_item` text not null, " +
                "`created_at` integer(8) not null" +
                ");");

        update("create table if not exists `expired_market_items` (" +
                "`owner` char(36) not null, " +
                "`market_item` text not null);");
    }
    

}
