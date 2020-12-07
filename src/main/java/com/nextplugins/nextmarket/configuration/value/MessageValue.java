package com.nextplugins.nextmarket.configuration.value;

import com.nextplugins.nextmarket.NextMarket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Henry Fábio
 */
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageValue {

    private static final MessageValue instance = new MessageValue();

    private final Configuration configuration = NextMarket.getInstance().getConfig();

    private final List<String> commandMessage = messageList("messages.command-message");
    private final String maximumValueReachedMessage = message("messages.maximum-value-reached")
            .replace("%amount%", String.valueOf(ConfigValue.<Double>get(ConfigValue::maximumAnnouncementValue)));
    private final String minimumValueNotReachedMessage = message("messages.minimum-value-not-reached")
            .replace("%amount%", String.valueOf(ConfigValue.<Double>get(ConfigValue::minimumAnnouncementValue)));
    private final String noPermissionMessage = message("messages.no-permission");
    private final String outOfBoundsMessage = message("messages.out-of-bounds");
    private final String offlinePlayerMessage = message("messages.player-offline");
    private final String cancelAnSellMessage = message("messages.cancel-a-sell");
    private final String expiredItemMessage = message("messages.expired-item");
    private final String boughtAnItemMessage = message("messages.bought-a-item");
    private final String soldAItemMessage = message("messages.sold-a-item");
    private final String announcedAItemMessage = message("messages.announced-a-item");
    private final String announcedAItemInPersonalMarket = message("messages.announced-a-item-in-personal-market");
    private final String insufficientMoneyMessage = message("messages.insufficient-money");
    private final String invalidItemMessage = message("messages.invalid-item");
    private final String fullInventoryMessage = message("messages.full-inventory");
    private final String sellingForYou = message("messages.selling-for-you");
    private final String categoryNotExists = message("messages.category-not-exists");
    private final String unavailableProduct = message("messages.unavailable-product");
    private final String announcementMessage = message("messages.announcement");
    private final String privateAnnouncementMessage = message("messages.private-announcement");
    private final String changedHandItemMessage = message("messages.changed-hand-item");

    public static <T> T get(Function<MessageValue, T> supplier) {
        return supplier.apply(MessageValue.instance);
    }

    private String colors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String message(String key) {
        return colors(configuration.getString(key));
    }

    private List<String> messageList(String key) {
        return configuration.getStringList(key)
                .stream()
                .map(this::colors)
                .collect(Collectors.toList());
    }

}
