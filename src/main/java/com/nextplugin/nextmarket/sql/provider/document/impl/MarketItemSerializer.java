package com.nextplugin.nextmarket.sql.provider.document.impl;

import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.sql.provider.document.Serializer;
import lombok.Cleanup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class MarketItemSerializer implements Serializer<MarketItem> {

    @Override
    public String serialize(MarketItem value) {
        String itemStack = serializeStack(value.getItemStack());

        return new StringBuilder()
                .append(itemStack)
                .append(";")
                .append(value.getSeller().getUniqueId())
                .append(";")
                .append(value.getCreateTime().getTime())
                .append(";")
                .append(value.getDestination().getUniqueId())
                .append(";")
                .append(value.getPrice())
                .toString();
    }

    @Override
    public MarketItem deserialize(String value) {
        String[] data = value.split(";");

        ItemStack itemStack = deserializeStack(data[0]);
        Player seller = Bukkit.getPlayer(UUID.fromString(data[1]));
        long time = Long.parseLong(data[2]);
        Player destination = Bukkit.getPlayer(UUID.fromString(data[3]));
        double price = Double.parseDouble(data[4]);

        return new MarketItem(seller.getName(), itemStack, price, new Date(time), destination.getName());
    }

    private ItemStack deserializeStack(String value) {
        try {
            @Cleanup ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(value));
            @Cleanup BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            return (ItemStack) dataInput.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String serializeStack(ItemStack stack) {
        try {
            @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            @Cleanup BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(stack);
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
