package com.nextplugin.nextmarket.api.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MarketItemCreateEvent extends Event implements Cancellable {

    private Player seller;
    private double price;
    private boolean isCancelled;
    private String cancellationReason;

    /**
     * The default constructor is defined for cleaner code. This constructor
     * assumes the event is synchronous.
     */
    public MarketItemCreateEvent(Player seller, double price) {
        this.seller = seller;
        this.price = price;
    }

    public Player getSeller() {
        return seller;
    }

    public void setSeller(Player seller) {
        this.seller = seller;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    /**
     * Convenience method for providing a user-friendly identifier. By
     * default, it is the event's class's {@linkplain Class#getSimpleName()
     * simple name}.
     *
     * @return name of this event
     */
    @Override
    public String getEventName() {
        return super.getEventName();
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
