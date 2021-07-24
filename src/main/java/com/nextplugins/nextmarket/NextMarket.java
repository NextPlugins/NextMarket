package com.nextplugins.nextmarket;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.nextmarket.api.NextMarketAPI;
import com.nextplugins.nextmarket.api.metric.MetricProvider;
import com.nextplugins.nextmarket.command.MarketCommand;
import com.nextplugins.nextmarket.configuration.ConfigurationLoader;
import com.nextplugins.nextmarket.dao.SQLProvider;
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
import me.saiintbrisson.bukkit.command.BukkitFrame;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class NextMarket extends JavaPlugin {

    @Getter private Injector injector;
    @Getter private SQLConnector sqlConnector;
    @Getter private SQLExecutor sqlExecutor;

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
        getLogger().info("Iniciando carregamento do plugin.");

        val loadTime = Stopwatch.createStarted();

        this.categoriesConfig = ConfigurationLoader.of("categories.yml").saveResource().create();
        InventoryManager.enable(this);

        sqlConnector = SQLProvider.of(this).setup();
        sqlExecutor = new SQLExecutor(sqlConnector);

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

        MetricProvider.of(this).register();

        this.injector.injectMembers(NextMarketAPI.getInstance());

        loadTime.stop();
        getLogger().log(Level.INFO, "Plugin inicializado com sucesso. ({0})", loadTime);
    }

    public static NextMarket getInstance() {
        return getPlugin(NextMarket.class);
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

}
