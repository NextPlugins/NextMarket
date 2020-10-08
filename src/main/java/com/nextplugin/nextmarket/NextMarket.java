package com.nextplugin.nextmarket;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.nextplugin.nextmarket.manager.CategoryManager;
import lombok.Getter;
import me.bristermitten.pdm.PDMBuilder;
import me.bristermitten.pdm.PluginDependencyManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Getter
public final class NextMarket extends JavaPlugin {

    private CompletableFuture<Void> dependencyLoader;

    private Injector injector;
    private FileConfiguration categoriesConfiguration;

    @Inject private CategoryManager categoryManager;

    public static NextMarket getInstance() {
        return getPlugin(NextMarket.class);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        loadCategoriesConfiguration();

        PluginDependencyManager dependencyManager = new PDMBuilder(this).build();
        this.dependencyLoader = dependencyManager.loadAllDependencies();
        dependencyLoader.thenRun(() -> {
            try {
                NextMarket instance = getInstance();
                this.injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(NextMarket.class).toInstance(instance);
                        bind(Logger.class).toInstance(instance.getLogger());
                    }
                });

                this.injector.injectMembers(instance);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onEnable() {
        System.out.println("abc");
        dependencyLoader.thenRun(() -> {
            System.out.println("ta aqui porra");
            System.out.println(this.categoryManager);
            this.categoryManager.registerCategories(
                    this.categoriesConfiguration.getConfigurationSection("categories")
            );
            System.out.println(this.categoryManager.getCategoryMap());
        });
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
