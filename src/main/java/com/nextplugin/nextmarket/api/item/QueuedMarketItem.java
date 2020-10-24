package com.nextplugin.nextmarket.api.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueuedMarketItem {

    private final MarketItem item;
    private final QueueItemOperation operation;

}