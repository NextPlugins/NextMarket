package com.nextplugins.nextmarket.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.henryfabio.sqlprovider.common.SQLProvider;
import com.nextplugins.nextmarket.NextMarket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.Configuration;

import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = false)
@Data(staticConstructor = "of")
public class PluginModule extends AbstractModule {

    private final NextMarket nextMarket;

    @Override
    protected void configure() {
        bind(NextMarket.class)
                .toInstance(nextMarket);
        bind(Logger.class)
                .annotatedWith(Names.named("main"))
                .toInstance(nextMarket.getLogger());

        bind(Configuration.class)
                .annotatedWith(Names.named("main"))
                .toInstance(nextMarket.getConfig());
        bind(Configuration.class)
                .annotatedWith(Names.named("categories"))
                .toInstance(nextMarket.getCategoriesConfig());

        bind(SQLProvider.class)
                .toInstance(nextMarket.getSqlProvider());
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

}
