package com.nextplugin.nextmarket;

import com.nextplugin.nextmarket.sql.MarketDatabaseAccessObject;
import com.nextplugin.nextmarket.sql.provider.ConnectionBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NextMarket extends JavaPlugin {

    public static NextMarket getInstance() {
        return getPlugin(NextMarket.class);
    }

    private FileConfiguration categoriesConfiguration;
    private ConnectionBuilder connectionBuilder;
    private MarketDatabaseAccessObject marketDatabaseAccessObject;

    @Override
    public void onLoad() {
        saveResource("categories.yml", false);

        categoriesConfiguration = YamlConfiguration.loadConfiguration(
                new File(getDataFolder(), "categories.yml")
        );
    }

    @Override
    public void onEnable() {
        connectionBuilder = new ConnectionBuilder()
                .driver(getString("database.driver"))
                .url(getString("database.url"))
                .password(getString("database.password"))
                .user(getString("database.user"));

        marketDatabaseAccessObject = new MarketDatabaseAccessObject(connectionBuilder);
        marketDatabaseAccessObject.create();
    }

    @Override
    public void onDisable() {
        marketDatabaseAccessObject.shutdown();
    }

    public FileConfiguration getCategoriesConfiguration() {
        return categoriesConfiguration;
    }

    private String getString(String key) {
        return getConfig().getString(key);
    }

}
