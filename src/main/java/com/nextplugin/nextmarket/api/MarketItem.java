package com.nextplugin.nextmarket.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MarketItem {

    private final ItemStack stack;
    private final Player player;
    private final long expire;
    private final double price;

    public MarketItem(ItemStack stack, Player player, long expire, double price) {
        this.stack = stack;
        this.player = player;
        this.expire = expire;
        this.price = price;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getStack() {
        return stack;
    }

    public long getExpire() {
        return expire;
    }

    public boolean isExpired() {
        return expire < System.currentTimeMillis();
    }

    public double getPrice() {
        return price;
    }

}
