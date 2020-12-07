package com.nextplugins.nextmarket.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.henryfabio.sqlprovider.common.SQLProvider;
import com.henryfabio.sqlprovider.common.executor.SQLExecutor;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.dao.adapter.ProductAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Henry Fábio
 */
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
//        return sqlProvider.executor().resultManyQuery(
//                "SELECT * FROM " + table,
//                ProductAdapter.class
//        );
        return Arrays.asList(
                Product.builder()
                        .seller(Bukkit.getOfflinePlayer(UUID.fromString("6c2fb127-6a3b-33fb-a26f-b1d09ca1f014")))
                        .itemStack(new ItemStack(Material.REDSTONE))
                        .price(10)
                        .build(),
                Product.builder()
                        .seller(Bukkit.getOfflinePlayer(UUID.fromString("b6366fef-14d2-3138-8c38-6e334b23bc46")))
                        .itemStack(new ItemStack(Material.DIAMOND_BOOTS))
                        .price(20)
                        .build(),
                Product.builder()
                        .seller(Bukkit.getOfflinePlayer(UUID.fromString("6c2fb127-6a3b-33fb-a26f-b1d09ca1f014")))
                        .itemStack(new ItemStack(Material.DIAMOND_CHESTPLATE))
                        .price(30)
                        .build(),
                Product.builder()
                        .seller(Bukkit.getOfflinePlayer(UUID.fromString("6c2fb127-6a3b-33fb-a26f-b1d09ca1f014")))
                        .destination(Bukkit.getOfflinePlayer(UUID.fromString("b6366fef-14d2-3138-8c38-6e334b23bc46")))
                        .itemStack(new ItemStack(Material.DIAMOND_AXE))
                        .price(5)
                        .build()
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
