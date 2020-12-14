package com.nextplugins.nextmarket.dao.adapter;

import com.henryfabio.sqlprovider.common.adapter.SQLAdapter;
import com.henryfabio.sqlprovider.common.result.SimpleResultSet;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public final class ProductAdapter implements SQLAdapter<Product> {

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

    @Override
    public void adaptStatement(PreparedStatement statement, Product target) throws SQLException {
        statement.setString(1, target.getUniqueId().toString());
        statement.setString(2, target.getSeller().getUniqueId().toString());

        OfflinePlayer destination = target.getDestination();
        statement.setString(3, destination != null ? destination.getUniqueId().toString() : null);

        statement.setString(4, ItemStackUtils.serialize(target.getItemStack()));
        statement.setDouble(5, target.getPrice());
        statement.setLong(6, target.getCreateAt().toEpochMilli());
    }

    private OfflinePlayer findDestination(SimpleResultSet resultSet) {
        try {
            return Bukkit.getOfflinePlayer(UUID.fromString(resultSet.get("destination")));
        } catch (Exception e) {
            return null;
        }
    }

}
