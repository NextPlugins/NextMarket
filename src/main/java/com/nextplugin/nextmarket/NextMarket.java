package com.nextplugin.nextmarket;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.henryfabio.inventoryapi.manager.InventoryManager;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.command.MarketCommand;
import com.nextplugin.nextmarket.hook.VaultHook;
import com.nextplugin.nextmarket.listeners.MarketEvents;
import com.nextplugin.nextmarket.manager.ButtonManager;
import com.nextplugin.nextmarket.manager.CategoryManager;
import com.nextplugin.nextmarket.manager.MarketItemManager;
import com.nextplugin.nextmarket.sql.MarketDAO;
import com.nextplugin.nextmarket.sql.connection.SQLConnection;
import com.nextplugin.nextmarket.sql.connection.mysql.MySQLConnection;
import com.nextplugin.nextmarket.sql.connection.sqlite.SQLiteConnection;
import lombok.Getter;
import me.bristermitten.pdm.PDMBuilder;
import me.bristermitten.pdm.PluginDependencyManager;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Getter
public final class NextMarket extends JavaPlugin {

    private CompletableFuture<Void> dependencyLoader;

    private Injector injector;

    private FileConfiguration categoriesConfiguration;
    private SQLConnection sqlConnection;

    @Inject private MarketDAO marketDAO;
    @Inject private MarketCache marketCache;

    @Inject private CategoryManager categoryManager;
    @Inject private MarketItemManager marketItemManager;
    @Inject private ButtonManager buttonManager;

    public static NextMarket getInstance() {
        return getPlugin(NextMarket.class);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        loadCategoriesConfiguration();

        PluginDependencyManager dependencyManager = new PDMBuilder(this).build();
        this.dependencyLoader = dependencyManager.loadAllDependencies();
        this.dependencyLoader.thenRun(() -> {
            configureSQLConnection();

            try {
                NextMarket instance = this;
                this.injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(NextMarket.class).toInstance(instance);
                        bind(Logger.class).annotatedWith(Names.named("main")).toInstance(instance.getLogger());
                        bind(SQLConnection.class).toInstance(instance.sqlConnection);
                    }
                });
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    public void onEnable() {
        this.dependencyLoader.thenRun(() -> {
            try {
                this.injector.injectMembers(this);

                this.categoryManager.registerCategories(
                        this.categoriesConfiguration.getConfigurationSection("categories")
                );

                this.buttonManager.registerButtons(
                        this.categoriesConfiguration.getConfigurationSection("inventory.main.buttons")
                );

                this.marketDAO.createTable();

                BukkitFrame bukkitFrame = new BukkitFrame(this);

                MarketCommand marketCommand = new MarketCommand();
                this.injector.injectMembers(marketCommand);
                bukkitFrame.registerCommands(marketCommand);

                InventoryManager.enable(this);

                registerEvents();
                registerVault();
                loadItems();

            } catch (Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void loadItems() {
        marketDAO.findAllMarketItemList().forEach(marketCache::addItem);
    }

    private void loadCategoriesConfiguration() {
        saveResource("categories.yml", false);

        categoriesConfiguration = YamlConfiguration.loadConfiguration(
                new File(getDataFolder(), "categories.yml")
        );
    }

    private void configureSQLConnection() {
        ConfigurationSection connectionSection = getConfig().getConfigurationSection("connection");
        this.sqlConnection = new MySQLConnection();
        if (!sqlConnection.configure(connectionSection.getConfigurationSection("mysql"))) {
            this.sqlConnection = new SQLiteConnection();
            this.sqlConnection.configure(connectionSection.getConfigurationSection("sqlite"));
        }
    }

    private void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        MarketEvents marketEvents = new MarketEvents();
        this.injector.injectMembers(marketEvents);
        pluginManager.registerEvents(marketEvents, this);
    }

    private void registerVault(){
        new VaultHook();
    }

}
