package com.nextplugins.nextmarket.api.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Henry Fábio
 */
public abstract class CustomEvent extends Event {

    @Getter private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
