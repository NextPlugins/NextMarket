package com.nextplugins.nextmarket.configuration.value;

import com.nextplugins.nextmarket.NextMarket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Function;

/**
 * @author Henry Fábio
 */
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigValue {

    private static final ConfigValue instance = new ConfigValue();

    private final Configuration configuration = NextMarket.getInstance().getConfig();

    private final ConfigurationSection sellLimit = configuration.getConfigurationSection("limits");

    private final double minimumAnnouncementValue = configuration.getDouble("announcement.minimum-value");
    private final double maximumAnnouncementValue = configuration.getDouble("announcement.maximum-value");
    private final double announcementPrice = configuration.getDouble("announcement.price");
    private final int announcementSecondsDelay = configuration.getInt("announcement.delay");
    private final int announcementExpireTime = configuration.getInt("announcement.expire-time");

    public static <T> T get(Function<ConfigValue, T> supplier) {
        return supplier.apply(ConfigValue.instance);
    }

}
