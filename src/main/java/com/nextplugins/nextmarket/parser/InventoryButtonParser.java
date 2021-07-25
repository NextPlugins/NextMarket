package com.nextplugins.nextmarket.parser;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.inventory.button.InventoryButton;
import com.nextplugins.nextmarket.util.ColorUtils;
import com.nextplugins.nextmarket.util.TypeUtil;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;

import java.util.stream.Collectors;

@Singleton
public final class InventoryButtonParser {

    public InventoryButton parse(ConfigurationSection section) {

        val itemStack = TypeUtil.convertFromLegacy(
                section.getString("material"),
                (byte) section.getInt("data"));

        return InventoryButton.builder()
                .displayName(ColorUtils.format(section.getString("displayName")))
                .lore(section.getStringList("lore").stream()
                        .map(ColorUtils::format)
                        .collect(Collectors.toList()))
                .materialData(itemStack == null ? new MaterialData(Material.BARRIER) : itemStack.getData())
                .inventorySlot(section.getInt("inventorySlot"))
                .build();
    }

}
