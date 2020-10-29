package com.nextplugin.nextmarket.api.event;

import com.nextplugin.nextmarket.api.item.MarketItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MarketItemSellEvent extends MarketEvent {

    private final Player player;
    private final MarketItem marketItem;

}
