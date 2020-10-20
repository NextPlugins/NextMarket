package com.nextplugin.nextmarket.parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.nextplugin.nextmarket.api.button.Button;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Logger;

@Singleton
public final class ButtonParser {

    @Inject @Named("main") private Logger logger;
    @Inject private MenuIconParser menuIconParser;

    public Button parseSection(ConfigurationSection section) {
        return Button.builder()
                .menu(section.getString("menu"))
                .displayName(section.getString("displayName"))
                .description(section.getStringList("description"))
                .icon(this.menuIconParser.parseSection(section.getConfigurationSection("icon")))
                .build();
    }
}
