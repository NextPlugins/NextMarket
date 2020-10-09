package com.nextplugin.nextmarket.configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugin.nextmarket.NextMarket;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Accessors(fluent = true)
@Getter
public class ConfigValue {

    @Inject private NextMarket market;

    private final FileConfiguration config = market.getConfig();

    private final double minimumAnnouncementValue = config.getDouble("announcement.minimum-value");
    private final double maximumAnnouncementValue = config.getDouble("announcement.maximum-value");
    private final int announcementSecondsDelay = config.getInt("announcement.delay");
    private final int announcementExpireTime = config.getInt("announcement.expire-time");
    private final double announcementPrice = config.getDouble("announcement.price");

    private final String announcementMessage =  translateColor(config.getString("announcement.message"));
    private final List<String> commandMessage =
            config
            .getStringList("command-message")
            .stream()
            .map(this::translateColor)
            .collect(Collectors.toList());
    private final String maximumValueReachedMessage = translateColor(config.getString("messages.maximum-value-reached"));
    private final String minimumValueNotReachedMessage = translateColor(config.getString("messages.minimu-value-not-reached"));
    private final String offlinePlayerMessage = translateColor(config.getString("message.player-offline"));
    private final String expiredItemMessage = translateColor(config.getString("message.expired-item"));
    private final String boughtAnItemMessage = translateColor(config.getString("message.bought-a-item"));
    private final String soldAItemMessage = translateColor(config.getString("message.sold-a-item"));
    private final String announcedAItemMessage = translateColor(config.getString("message.announced-a-item"));
    private final String insufficientMoneyMessage = translateColor(config.getString("message.insufficient-money"));

    private String translateColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
