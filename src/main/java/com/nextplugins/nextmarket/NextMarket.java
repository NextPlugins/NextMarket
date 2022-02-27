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
import com.nextplugins.nextmarket.listener.UpdateCheckerListener;
import com.nextplugins.nextmarket.manager.CategoryManager;
import com.nextplugins.nextmarket.manager.ProductManager;
import com.nextplugins.nextmarket.registry.InventoryButtonRegistry;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.util.MessageUtils;
import com.yuhtin.updatechecker.UpdateChecker;
import lombok.Getter;
import lombok.val;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageType;
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

    @Getter private UpdateChecker updateChecker;

    @Override
    public void onLoad() {
        updateChecker = new UpdateChecker(this, "NextPlugins");
        updateChecker.check();

        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        getLogger().info("Iniciando carregamento do plugin.");

        val loadTime = Stopwatch.createStarted();
        if (updateChecker.canUpdate()) {
            val lastRelease = updateChecker.getLastRelease();

            getLogger().info("");
            getLogger().info("[NextUpdate] ATENÇÃO!");
            getLogger().info("[NextUpdate] Você está usando uma versão antiga do NextMarket!");
            getLogger().info("[NextUpdate] Nova versão: " + lastRelease.getVersion());
            getLogger().info("[NextUpdate] Baixe aqui: " + lastRelease.getDownloadURL());
            getLogger().info("");
        } else {
            getLogger().info("[NextUpdate] Olá! Vim aqui revisar se a versão do NextMarket está atualizada, e pelo visto sim! Obrigado por usar nossos plugins!");
        }

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
        registerListener(UpdateCheckerListener.class);

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
        bukkitFrame.getMessageHolder().setMessage(MessageType.INCORRECT_USAGE, MessageUtils.colored("&cUso incorreto, use: &f{usage}&c."));
        bukkitFrame.registerCommands(
                injector.getInstance(MarketCommand.class)
        );
    }

    private void registerListener(Class<? extends Listener> clazz) {
        Bukkit.getPluginManager().registerEvents(injector.getInstance(clazz), this);
    }

}
