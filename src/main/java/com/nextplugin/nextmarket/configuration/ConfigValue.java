package com.nextplugin.nextmarket.configuration;

import com.nextplugin.nextmarket.NextMarket;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@Getter
public class ConfigValue {

    private FileConfiguration config;

    private final double minimumAnnouncementValue;
    private final double maximumAnnouncementValue;
    private final double announcementPrice;
    private final int announcementSecondsDelay;
    private final int announcementExpireTime;

    private final String announcementMessage;
    private final List<String> commandMessage;
    private final String maximumValueReachedMessage;
    private final String minimumValueNotReachedMessage;
    private final String offlinePlayerMessage;
    private final String expiredItemMessage;
    private final String boughtAnItemMessage;
    private final String soldAItemMessage;
    private final String announcedAItemMessage;
    private final String insufficientMoneyMessage;

    public ConfigValue(NextMarket market) {
        config = market.getConfig();

        minimumAnnouncementValue = config.getDouble("announcement.minimum-value");

        maximumAnnouncementValue = config.getDouble("announcement.maximum-value");
        announcementPrice = config.getDouble("announcement.price");
        announcementSecondsDelay = config.getInt("announcement.delay");
        announcementExpireTime = config.getInt("announcement.expire-time");

        announcementMessage = getTranslatedString("announcement.message");
        commandMessage = config.getStringList("command-message")
                        .stream()
                        .map(this::translateColor)
                        .collect(Collectors.toList());
        maximumValueReachedMessage = getTranslatedString("messages.maximum-value-reached");
        minimumValueNotReachedMessage = getTranslatedString("messages.minimum-value-not-reached");
        offlinePlayerMessage = getTranslatedString("messages.player-offline");
        expiredItemMessage = getTranslatedString("messages.expired-item");
        boughtAnItemMessage = getTranslatedString("messages.bought-a-item");
        soldAItemMessage = getTranslatedString("messages.sold-a-item");
        announcedAItemMessage = getTranslatedString("messages.announced-a-item");
        insufficientMoneyMessage = getTranslatedString("messages.insufficient-money");
    }

    private String translateColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String getTranslatedString(String key) {
        return translateColor(config.getString(key));
    }

}
