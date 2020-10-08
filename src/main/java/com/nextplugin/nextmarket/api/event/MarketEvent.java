package com.nextplugin.nextmarket.api.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public abstract class MarketEvent extends Event {

    @Getter private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }


}
