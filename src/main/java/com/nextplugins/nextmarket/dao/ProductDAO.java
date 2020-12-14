package com.nextplugins.nextmarket.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.henryfabio.sqlprovider.common.SQLProvider;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.dao.adapter.ProductAdapter;

import java.util.List;

@Singleton
public final class ProductDAO {

    private final String table = "market_products";
    @Inject private SQLProvider sqlProvider;

    public void createTable() {
        sqlProvider.executor().updateQuery("CREATE TABLE IF NOT EXISTS " + table + "(" +
                "uniqueId VARCHAR(36) NOT NULL PRIMARY KEY," +
                "seller VARCHAR(36) NOT NULL," +
                "destination VARCHAR(36)," +
                "itemStack TEXT NOT NULL," +
                "price DOUBLE NOT NULL," +
                "createAt INTEGER(16) NOT NULL" +
                ");");
    }

    public List<Product> selectAll() {
        return sqlProvider.executor().resultManyQuery(
                "SELECT * FROM " + table,
                ProductAdapter.class
        );
    }

    public void insertOne(Product product) {
        sqlProvider.executor().updateOneQuery(
                "INSERT INTO " + table + " VALUES(?,?,?,?,?,?);",
                ProductAdapter.class,
                product
        );
    }

    public void deleteOne(Product product) {
        sqlProvider.executor().updateQuery(
                "DELETE FROM " + table + " WHERE uniqueId = '" + product.getUniqueId() + "'"
        );
    }

}
