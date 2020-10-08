package com.nextplugin.nextmarket;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.nextplugin.nextmarket.manager.CategoryManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

@Getter
public final class NextMarket extends JavaPlugin {

    public static NextMarket getInstance() {
        return getPlugin(NextMarket.class);
    }

    private Injector injector;
    private FileConfiguration categoriesConfiguration;

    @Inject private CategoryManager categoryManager;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        loadCategoriesConfiguration();

        NextMarket instance = this;
        this.injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NextMarket.class).toInstance(instance);
                bind(Logger.class).toInstance(instance.getLogger());
            }
        });

        this.injector.injectMembers(this);
    }

    @Override
    public void onEnable() {
        this.categoryManager.registerCategories(
                this.categoriesConfiguration.getConfigurationSection("categories")
        );
    }

    @Override
    public void onDisable() {

    }

    private void loadCategoriesConfiguration() {
        saveResource("categories.yml", false);

        categoriesConfiguration = YamlConfiguration.loadConfiguration(
                new File(getDataFolder(), "categories.yml")
        );
    }

}
