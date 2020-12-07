package com.nextplugins.nextmarket.configuration.value;

import com.nextplugins.nextmarket.NextMarket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Henry Fábio
 */
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InventoryValue {

    private static final InventoryValue instance = new InventoryValue();

    private final Configuration configuration = NextMarket.getInstance().getCategoriesConfig();

    private final String mainInventoryTitle = message("inventory.main.title");
    private final int mainInventoryLines = configuration.getInt("inventory.main.lines");

    private final String categoryInventoryTitle = message("inventory.category.title");
    private final int categoryInventoryLines = configuration.getInt("inventory.category.lines");

    private final String announcedInventoryTitle = message("inventory.announced.title");
    private final int announcedInventoryLines = configuration.getInt("inventory.announced.lines");

    private final String privateInventoryTitle = message("inventory.private.title");
    private final int privateInventoryLines = configuration.getInt("inventory.private.lines");

    private final List<String> categoryInventoryItemLore = messageList("inventory.category.lore");
    private final List<String> announcedInventoryItemLore = messageList("inventory.announced.lore");
    private final List<String> privateInventoryItemLore = messageList("inventory.private.lore");

    public static <T> T get(Function<InventoryValue, T> supplier) {
        return supplier.apply(InventoryValue.instance);
    }

    private String colors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String message(String key) {
        return colors(Objects.requireNonNull(configuration).getString(key));
    }

    private List<String> messageList(String key) {
        return Objects.requireNonNull(configuration).getStringList(key)
                .stream()
                .map(this::colors)
                .collect(Collectors.toList());
    }

}
