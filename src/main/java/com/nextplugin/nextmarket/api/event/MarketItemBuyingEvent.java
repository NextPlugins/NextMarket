package com.nextplugin.nextmarket.api.event;

import com.nextplugin.nextmarket.api.item.MarketItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MarketItemBuyingEvent extends MarketEvent implements Cancellable {

    private final Player player;
    private final MarketItem marketItem;
    private boolean cancelled;

}