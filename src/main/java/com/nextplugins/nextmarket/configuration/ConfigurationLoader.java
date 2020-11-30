package com.nextplugins.nextmarket.configuration;

import com.nextplugins.nextmarket.NextMarket;
import lombok.Data;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * @author Henry Fábio
 */
@Data(staticConstructor = "of")
public final class ConfigurationLoader {

    private final String path;

    public ConfigurationLoader saveResource() {
        NextMarket instance = NextMarket.getInstance();
        instance.saveResource(this.path, false);
        return this;
    }

    public Configuration create() {
        NextMarket instance = NextMarket.getInstance();
        return YamlConfiguration.loadConfiguration(new File(
                instance.getDataFolder(), this.path
        ));
    }

}
