package com.nextplugin.nextmarket.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugin.nextmarket.api.button.Button;
import com.nextplugin.nextmarket.parser.ButtonParser;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Singleton
public class ButtonManager {

    private final Map<String, Button> buttonMap = new LinkedHashMap<>();

    @Inject private ButtonParser buttonParser;

    public void registerButton(Button button) {
        this.buttonMap.put(button.getMenu(), button);
    }

    public void registerButtons(ConfigurationSection section) {
        for (String sectionName : section.getKeys(false)) {
            ConfigurationSection categorySection = section.getConfigurationSection(sectionName);
            registerButton(this.buttonParser.parseSection(categorySection));
        }
    }

}
