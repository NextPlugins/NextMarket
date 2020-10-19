package com.nextplugin.nextmarket.api.item;

import com.nextplugin.nextmarket.configuration.ConfigValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Builder
@Data
@AllArgsConstructor
public class MarketItem {

    private final UUID sellerId;

    private final ItemStack itemStack;
    private final double price;

    @Builder.Default private final Date createTime = new Date();
    private final UUID destinationId;

    public OfflinePlayer getSeller() {
        return Bukkit.getOfflinePlayer(this.sellerId);
    }

    public OfflinePlayer getDestination() {
        return this.destinationId != null ? Bukkit.getOfflinePlayer(this.destinationId) : null;
    }

    public boolean isExpired() {
        long expireTime = ConfigValue.get(ConfigValue::announcementExpireTime);
        long time = this.createTime.getTime();
        return (time + TimeUnit.SECONDS.toMillis(expireTime)) <= System.currentTimeMillis();
    }


}
