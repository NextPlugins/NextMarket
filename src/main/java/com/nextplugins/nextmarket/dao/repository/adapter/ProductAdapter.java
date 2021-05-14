package com.nextplugins.nextmarket.dao.repository.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.UUID;

public final class ProductAdapter implements SQLResultAdapter<Product> {

    @Override
    public Product adaptResult(SimpleResultSet resultSet) {
        return Product.builder()
                .uniqueId(UUID.fromString(resultSet.get("uniqueId")))
                .seller(Bukkit.getOfflinePlayer(UUID.fromString(resultSet.get("seller"))))
                .destination(this.findDestination(resultSet))
                .itemStack(ItemStackUtils.deserialize(resultSet.get("itemStack")))
                .price(resultSet.get("price"))
                .createAt(Instant.ofEpochMilli(resultSet.get("createAt")))
                .build();
    }

    private OfflinePlayer findDestination(SimpleResultSet resultSet) {
        try {
            return Bukkit.getOfflinePlayer(UUID.fromString(resultSet.get("destination")));
        } catch (Exception e) {
            return null;
        }
    }

}
