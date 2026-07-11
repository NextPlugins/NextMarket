package com.nextplugins.nextmarket.parser;

import com.cryptomorin.xseries.XMaterial;
import com.google.inject.Singleton;
import com.nextplugins.nextmarket.api.model.product.MaterialData;
import com.nextplugins.nextmarket.compat.Items;
import com.nextplugins.nextmarket.inventory.button.InventoryButton;
import com.nextplugins.nextmarket.util.MessageUtils;
import lombok.val;
import org.bukkit.configuration.ConfigurationSection;

import java.util.stream.Collectors;

@Singleton
public final class InventoryButtonParser {

    public InventoryButton parse(ConfigurationSection section) {
        String materialName = section.getString("material", "BARRIER");
        int data = section.getInt("data", 0);

        MaterialData materialData = Items.parseMaterialData(
                data > 0 ? materialName + ":" + data : materialName
        );
        if (materialData == null) {
            materialData = Items.parseMaterialData(materialName);
        }
        if (materialData == null) {
            materialData = new MaterialData(XMaterial.BARRIER, null, true);
        }

        return InventoryButton.builder()
                .displayName(MessageUtils.colored(section.getString("displayName")))
                .lore(section.getStringList("lore").stream()
                        .map(MessageUtils::colored)
                        .collect(Collectors.toList()))
                .materialData(materialData)
                .inventorySlot(section.getInt("inventorySlot"))
                .build();
    }

}
