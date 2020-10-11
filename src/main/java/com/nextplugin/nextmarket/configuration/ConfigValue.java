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

    private static final ConfigValue instance = new ConfigValue();

    private final FileConfiguration configuration = NextMarket.getInstance().getConfig();

    private final double minimumAnnouncementValue = configuration.getDouble("announcement.minimum-value");
    private final double maximumAnnouncementValue = configuration.getDouble("announcement.maximum-value");
    private final double announcementPrice = configuration.getDouble("announcement.price");
    private final int announcementSecondsDelay = configuration.getInt("announcement.delay");
    private final int announcementExpireTime = configuration.getInt("announcement.expire-time");

    private final String announcementMessage = translateMessage("announcement.message");
    private final List<String> commandMessage = translateMessageList("command-message");
    private final String maximumValueReachedMessage = translateMessage("messages.maximum-value-reached")
            .replace("%valor%", String.valueOf(maximumAnnouncementValue));
    private final String minimumValueNotReachedMessage = translateMessage("messages.minimum-value-not-reached")
            .replace("%valor%", String.valueOf(minimumAnnouncementValue));
    private final String offlinePlayerMessage = translateMessage("messages.player-offline");
    private final String expiredItemMessage = translateMessage("messages.expired-item");
    private final String boughtAnItemMessage = translateMessage("messages.bought-a-item");
    private final String soldAItemMessage = translateMessage("messages.sold-a-item");
    private final String announcedAItemMessage = translateMessage("messages.announced-a-item");
    private final String insufficientMoneyMessage = translateMessage("messages.insufficient-money");

//    public ConfigValue(NextMarket market) {
//        configuration = market.getConfig();
//
//        minimumAnnouncementValue = configuration.getDouble("announcement.minimum-value");
//
//        maximumAnnouncementValue = configuration.getDouble("announcement.maximum-value");
//        announcementPrice = configuration.getDouble("announcement.price");
//        announcementSecondsDelay = configuration.getInt("announcement.delay");
//        announcementExpireTime = configuration.getInt("announcement.expire-time");
//
//        announcementMessage = translateMessage("announcement.message");
//        commandMessage = translateMessageList("command-message");
//        maximumValueReachedMessage = translateMessage("messages.maximum-value-reached").replace("%valor%", String.valueOf(maximumAnnouncementValue));
//        minimumValueNotReachedMessage = translateMessage("messages.minimum-value-not-reached").replace("%valor%", String.valueOf(minimumAnnouncementValue));
//        offlinePlayerMessage = translateMessage("messages.player-offline");
//        expiredItemMessage = translateMessage("messages.expired-item");
//        boughtAnItemMessage = translateMessage("messages.bought-a-item");
//        soldAItemMessage = translateMessage("messages.sold-a-item");
//        announcedAItemMessage = translateMessage("messages.announced-a-item");
//        insufficientMoneyMessage = translateMessage("messages.insufficient-money");
//    }

    public static <T> T get(ValueSupplier<T> supplier) {
        return supplier.get(ConfigValue.instance);
    }

    private String translateColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String translateMessage(String key) {
        return translateColor(configuration.getString(key));
    }

    private List<String> translateMessageList(String key) {
        return configuration.getStringList(key)
                .stream()
                .map(this::translateColor)
                .collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface ValueSupplier<T> {

        T get(ConfigValue configValue);

    }

}
