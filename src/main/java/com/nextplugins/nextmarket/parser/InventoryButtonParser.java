package com.nextplugins.nextmarket.parser;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.inventory.button.InventoryButton;
import com.nextplugins.nextmarket.util.ColorUtils;
import com.nextplugins.nextmarket.util.TypeUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;

import java.util.stream.Collectors;

@Singleton
public final class InventoryButtonParser {

    public InventoryButton parse(ConfigurationSection section) {
        return InventoryButton.builder()
                .displayName(ColorUtils.format(section.getString("displayName")))
                .lore(section.getStringList("lore").stream()
                        .map(ColorUtils::format)
                        .collect(Collectors.toList()))
                .materialData(new MaterialData(
                        TypeUtil.getType(section.getString("material")),
                        (byte) section.getInt("data")))
                .inventorySlot(section.getInt("inventorySlot"))
                .build();
    }

}
