package com.nextplugin.nextmarket.api.event;

import com.nextplugin.nextmarket.api.item.MarketItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

@EqualsAndHashCode(callSuper = true)
@Data
public class MarketItemBuyedEvent extends MarketEvent {

    private final Player player;
    private final MarketItem marketItem;

}
