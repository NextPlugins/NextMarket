package com.nextplugin.nextmarket;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.command.MarketCommand;
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
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
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

    @Inject private CategoryManager categoryManager;
    @Inject private MarketItemManager marketItemManager;

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

                this.marketDAO.createTable();

                BukkitFrame bukkitFrame = new BukkitFrame(this);

                MarketCommand marketCommand = new MarketCommand();
                this.injector.injectMembers(marketCommand);
                bukkitFrame.registerCommands(marketCommand);
            } catch (Throwable t) {
                t.printStackTrace();
            }
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

    private void configureSQLConnection() {
        ConfigurationSection connectionSection = getConfig().getConfigurationSection("connection");
        this.sqlConnection = new MySQLConnection();
        if (!sqlConnection.configure(connectionSection.getConfigurationSection("mysql"))) {
            this.sqlConnection = new SQLiteConnection();
            this.sqlConnection.configure(connectionSection.getConfigurationSection("sqlite"));
        }
    }

}
