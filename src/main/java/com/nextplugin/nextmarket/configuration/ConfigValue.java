package com.nextplugin.nextmarket.configuration;

import com.nextplugin.nextmarket.NextMarket;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@Getter
public class ConfigValue implements ConfigImplementation {

    private static final ConfigValue instance = new ConfigValue();

    private final FileConfiguration configuration = NextMarket.getInstance().getConfig();

    private final ConfigurationSection sellLimit = configuration.getConfigurationSection("limits");

    private final double minimumAnnouncementValue = configuration.getDouble("announcement.minimum-value");
    private final double maximumAnnouncementValue = configuration.getDouble("announcement.maximum-value");
    private final double announcementPrice = configuration.getDouble("announcement.price");
    private final int announcementSecondsDelay = configuration.getInt("announcement.delay");
    private final int announcementExpireTime = configuration.getInt("announcement.expire-time");
    private final int sellTiming = configuration.getInt("announcement.sell-timing");

    private final String announcementMessage = translateMessage("announcement.message");
    private final List<String> commandMessage = translateMessageList("command-message");
    private final String maximumValueReachedMessage = translateMessage("messages.maximum-value-reached")
            .replace("%amount%", String.valueOf(maximumAnnouncementValue));
    private final String minimumValueNotReachedMessage = translateMessage("messages.minimum-value-not-reached")
            .replace("%amount%", String.valueOf(minimumAnnouncementValue));
    private final String noPermissionMessage = translateMessage("messages.no-permission");
    private final String outOfBoundsMessage = translateMessage("messages.out-of-bounds");
    private final String offlinePlayerMessage = translateMessage("messages.player-offline");
    private final String cancelAnSellMessage = translateMessage("messages.cancel-a-sell");
    private final String expiredItemMessage = translateMessage("messages.expired-item");
    private final String boughtAnItemMessage = translateMessage("messages.bought-a-item");
    private final String soldAItemMessage = translateMessage("messages.sold-a-item");
    private final String announcedAItemMessage = translateMessage("messages.announced-a-item");
    private final String announcedAItemInPersonalMarket = translateMessage("messages.announced-a-item-in-personal-market");
    private final String insufficientMoneyMessage = translateMessage("messages.insufficient-money");
    private final String invalidItemMessage = translateMessage("messages.invalid-item");
    private final String openingInventoryMessage = translateMessage("messages.opening-inventory");
    private final String fullInventoryMessage = translateMessage("messages.full-inventory");
    private final String inSellTiming = translateMessage("messages.in-sell-timing");

    public static <T> T get(ValueSupplier<T> supplier) {
        return supplier.get(ConfigValue.instance);
    }

    @Override
    public String translateColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public String translateMessage(String key) {
        return translateColor(configuration.getString(key));
    }

    @Override
    public List<String> translateMessageList(String key) {
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
