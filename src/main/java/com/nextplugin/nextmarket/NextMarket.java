package com.nextplugin.nextmarket;

import com.nextplugin.nextmarket.api.Market;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class NextMarket extends JavaPlugin {

    public static NextMarket getInstance() {
        return getPlugin(NextMarket.class);
    }

    private Market market;
    private FileConfiguration categoriesConfiguration;

    @Override
    public void onLoad() {
        saveResource("categories.yml", false);

        market = new Market(this);
        categoriesConfiguration = YamlConfiguration.loadConfiguration(
                new File(getDataFolder(), "categories.yml")
        );
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public Market getMarket() {
        return market;
    }

    public FileConfiguration getCategoriesConfiguration() {
        return categoriesConfiguration;
    }

}
