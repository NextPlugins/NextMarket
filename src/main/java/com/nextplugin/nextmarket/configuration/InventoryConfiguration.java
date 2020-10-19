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
public class InventoryConfiguration implements ConfigImplementation {

    private static final InventoryConfiguration instance = new InventoryConfiguration();

    private final FileConfiguration configuration = NextMarket.getInstance().getCategoriesConfiguration();

    private final String mainInventoryTitle = configuration.getString("inventory.main.title");
    private final int mainInventoryLines = configuration.getInt("inventory.main.lines");

    private final String categoryInventoryTitle = configuration.getString("inventory.category.title");
    private final int categoryInventoryLines = configuration.getInt("inventory.category.lines");

    public static <T> T get(CategorySupplier<T> supplier) {
        return supplier.get(InventoryConfiguration.instance);
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
    public interface CategorySupplier<T> {
        T get(InventoryConfiguration categoryConfiguration);
    }

}
