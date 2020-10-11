package com.nextplugin.nextmarket.sql.provider.document.impl;

import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.sql.provider.document.Document;
import com.nextplugin.nextmarket.sql.provider.document.Serializer;
import com.nextplugin.nextmarket.util.ItemStackUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MarketItemSerializer implements Serializer<MarketItem> {

    @Getter private static final MarketItemSerializer instance = new MarketItemSerializer();

    @Override
    public String serialize(MarketItem marketItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MarketItem deserialize(Document document) {
        String destinationAsString = document.getString("destination");
        System.out.println("uuid: " + destinationAsString);
        return MarketItem.builder()
                .sellerId(UUID.fromString(document.getString("seller")))
                .itemStack(ItemStackUtil.deserialize(document.getString("itemStack")))
                .price(document.getNumber("price").doubleValue())
                .createTime(new Date(document.getNumber("created_at").longValue()))
                .destinationId(destinationAsString != null ? UUID.fromString(destinationAsString) : null)
                .build();
    }

//    @Override
//    public MarketItem deserialize(String value) {
//        String[] data = value.split(";");
//
//        ItemStack itemStack = deserializeStack(data[0]);
//        Player seller = Bukkit.getPlayer(UUID.fromString(data[1]));
//        long time = Long.parseLong(data[2]);
//        double price = Double.parseDouble(data[4]);
//
//        UUID destinationId = null;
//        if (!data[3].equalsIgnoreCase("none")) {
//            destinationId = UUID.fromString(data[3]);
//        }
//
//        return new MarketItem(UUID.fromString(data[1]), itemStack, price, new Date(time), destinationId);
//    }

}
