package com.nextplugins.nextmarket.dao.repository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.dao.repository.adapter.ProductAdapter;
import com.nextplugins.nextmarket.util.ItemStackUtils;
import org.bukkit.OfflinePlayer;

import java.util.Set;

@Singleton
public final class ProductRepository {

    private static final String TABLE = "market_products";
    @Inject private SQLExecutor sqlExecutor;

    public void createTable() {
        sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE + "(" +
                "uniqueId VARCHAR(36) NOT NULL PRIMARY KEY," +
                "seller VARCHAR(36) NOT NULL," +
                "destination VARCHAR(36)," +
                "itemStack TEXT NOT NULL," +
                "price DOUBLE NOT NULL," +
                "createAt BIGINT NOT NULL" +
                ");");
    }

    public Set<Product> selectAll() {
        return sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE,
                k -> {},
                ProductAdapter.class
        );
    }

    public void insertOne(Product target) {
        sqlExecutor.updateQuery(
                "INSERT INTO " + TABLE + " VALUES(?,?,?,?,?,?);",
                statement -> {

                    statement.set(1, target.getUniqueId().toString());
                    statement.set(2, target.getSeller().getUniqueId().toString());

                    OfflinePlayer destination = target.getDestination();
                    statement.set(3, destination != null ? destination.getUniqueId().toString() : null);

                    statement.set(4, ItemStackUtils.serialize(target.getItemStack()));
                    statement.set(5, target.getPrice());
                    statement.set(6, target.getCreateAt().toEpochMilli());

                }
        );
    }

    public void deleteOne(Product product) {
        sqlExecutor.updateQuery(
                "DELETE FROM " + TABLE + " WHERE uniqueId = '" + product.getUniqueId() + "'"
        );
    }

}
