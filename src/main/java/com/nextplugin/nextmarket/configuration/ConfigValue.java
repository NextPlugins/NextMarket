package com.nextplugin.nextmarket.configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugin.nextmarket.NextMarket;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@Singleton
@Accessors(fluent = true)
@Getter
public class ConfigValue {

    @Inject private NextMarket market;

    private final FileConfiguration config = market.getConfig();

    private final String announcementMessage = ChatColor.translateAlternateColorCodes('&', config.getString("announcement.message"));
    private final int announcementSecondsDelay = config.getInt("announcement.delay");
    private final int announcementExpireTime = config.getInt("announcement.expire-time");
    private final double announcementPrice = config.getDouble("announcement.price");

}
