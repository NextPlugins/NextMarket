package com.nextplugins.nextmarket;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.common.SQLProvider;
import com.henryfabio.sqlprovider.mysql.MySQLProvider;
import com.henryfabio.sqlprovider.mysql.configuration.MySQLConfiguration;
import com.henryfabio.sqlprovider.sqlite.SQLiteProvider;
import com.henryfabio.sqlprovider.sqlite.configuration.SQLiteConfiguration;
import com.nextplugins.nextmarket.api.NextMarketAPI;
import com.nextplugins.nextmarket.command.MarketCommand;
import com.nextplugins.nextmarket.configuration.ConfigurationLoader;
import com.nextplugins.nextmarket.configuration.value.ConfigValue;
import com.nextplugins.nextmarket.guice.PluginModule;
import com.nextplugins.nextmarket.hook.EconomyHook;
import com.nextplugins.nextmarket.listener.ProductBuyListener;
import com.nextplugins.nextmarket.listener.ProductCreateListener;
import com.nextplugins.nextmarket.listener.ProductRemoveListener;
import com.nextplugins.nextmarket.manager.CategoryManager;
import com.nextplugins.nextmarket.manager.ProductManager;
import com.nextplugins.nextmarket.registry.InventoryButtonRegistry;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import lombok.Getter;
import lombok.val;
import me.bristermitten.pdm.PluginDependencyManager;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NextMarket extends JavaPlugin {

    @Getter private Injector injector;
    @Getter private SQLProvider sqlProvider;

    @Inject @Named("main") private Logger logger;

    @Inject private EconomyHook economyHook;

    @Inject private CategoryManager categoryManager;
    @Inject private ProductManager productManager;

    @Inject private InventoryRegistry inventoryRegistry;
    @Inject private InventoryButtonRegistry inventoryButtonRegistry;

    @Getter private Configuration categoriesConfig;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        getLogger().info("Baixando e carregando dependências necessárias...");

        val downloadTime = Stopwatch.createStarted();

        PluginDependencyManager.of(this)
                .loadAllDependencies()
                .exceptionally(throwable -> {

                    throwable.printStackTrace();

                    getLogger().severe("Ocorreu um erro durante a inicialização do plugin!");
                    Bukkit.getPluginManager().disablePlugin(this);

                    return null;

                })
                .join();

        downloadTime.stop();

        getLogger().log(Level.INFO, "Dependências carregadas com sucesso! ({0})", downloadTime);
        getLogger().info("Iniciando carregamento do plugin.");

        val loadTime = Stopwatch.createStarted();

        this.categoriesConfig = ConfigurationLoader.of("categories.yml").saveResource().create();
        InventoryManager.enable(this);

        configureSqlProvider();
        this.sqlProvider.connect();

        this.injector = PluginModule.of(this).createInjector();
        this.injector.injectMembers(this);

        economyHook.init();

        categoryManager.init();
        productManager.init();

        inventoryRegistry.init();
        inventoryButtonRegistry.init();

        enableCommandFrame();

        registerListener(ProductCreateListener.class);
        registerListener(ProductRemoveListener.class);
        registerListener(ProductBuyListener.class);

        configureBStats();

        this.injector.injectMembers(NextMarketAPI.getInstance());

        loadTime.stop();
        getLogger().log(Level.INFO, "Plugin inicializado com sucesso. ({0})", loadTime);
    }

    public static NextMarket getInstance() {
        return getPlugin(NextMarket.class);
    }

    private void configureSqlProvider() {
        FileConfiguration configuration = getConfig();
        if (configuration.getBoolean("connection.mysql.enable")) {
            ConfigurationSection mysqlSection = configuration.getConfigurationSection("connection.mysql");
            sqlProvider = new MySQLProvider(new MySQLConfiguration()
                    .address(mysqlSection.getString("address"))
                    .username(mysqlSection.getString("username"))
                    .password(mysqlSection.getString("password"))
                    .database(mysqlSection.getString("database"))
            );
        } else {
            ConfigurationSection sqliteSection = configuration.getConfigurationSection("connection.sqlite");
            sqlProvider = new SQLiteProvider(new SQLiteConfiguration()
                    .file(new File(this.getDataFolder(), sqliteSection.getString("file")))
            );
        }
    }

    private void enableCommandFrame() {
        BukkitFrame bukkitFrame = new BukkitFrame(this);
        bukkitFrame.registerCommands(
                injector.getInstance(MarketCommand.class)
        );
    }

    private void registerListener(Class<? extends Listener> clazz) {
        Bukkit.getPluginManager().registerEvents(injector.getInstance(clazz), this);
    }

    private void configureBStats() {
        if (ConfigValue.get(ConfigValue::useBStats)) {
            int pluginId = 9933;
            new Metrics(this, pluginId);
            logger.info("Integração com o bStats configurada com sucesso.");
        } else {
            logger.info("Você desabilitou o uso do bStats, portanto, não utilizaremos dele.");
        }
    }

}
