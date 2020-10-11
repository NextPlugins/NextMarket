package com.nextplugin.nextmarket.sql.provider.document.parser.impl;

import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.sql.provider.document.Document;
import com.nextplugin.nextmarket.sql.provider.document.parser.DocumentParser;
import com.nextplugin.nextmarket.util.ItemStackUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MarketItemDocumentParser implements DocumentParser<MarketItem> {

    @Getter private static final MarketItemDocumentParser instance = new MarketItemDocumentParser();

    @Override
    public MarketItem parse(Document document) {
        String destinationAsString = document.getString("destination");
        return MarketItem.builder()
                .sellerId(UUID.fromString(document.getString("seller")))
                .itemStack(ItemStackUtil.deserialize(document.getString("itemStack")))
                .price(document.getNumber("price").doubleValue())
                .createTime(new Date(document.getNumber("created_at").longValue()))
                .destinationId(destinationAsString != null ? UUID.fromString(destinationAsString) : null)
                .build();
    }

}
