package com.nextplugin.nextmarket.parser;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.nextplugin.nextmarket.api.item.MenuIcon;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@Singleton
public final class MenuIconParser {

    @Inject @Named("main") private Logger logger;

    public MenuIcon parseSection(ConfigurationSection section) {
        return MenuIcon.builder()
                .itemStack(parseItemSection(section))
                .position(section.getInt("inventorySlot"))
                .build();
    }

    private ItemStack parseItemSection(ConfigurationSection section) {
        try {
            return new ItemStack(
                    Material.valueOf(section.getString("material")),
                    1,
                    (short) section.getInt("data")
            );
        } catch (Throwable t) {
            this.logger.warning(section.getParent().getName() + " is invalid!");
            return null;
        }
    }

}
