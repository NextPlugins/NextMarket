package com.nextplugin.nextmarket.api.item;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Builder
@Data
public class MarketItem {

    private final String sellerName;

    private final ItemStack itemStack;
    private final double price;

    private final Date createTime;
    private final String destination;

    public Player getSeller() {
        return Bukkit.getPlayer(this.sellerName);
    }

    public Player getDestination() {
        return this.destination != null ? Bukkit.getPlayer(this.destination) : null;
    }

    public boolean isExpired(long expireSeconds) {
        long time = this.createTime.getTime();
        return (time + TimeUnit.SECONDS.toMillis(expireSeconds)) <= System.currentTimeMillis();
    }

}
