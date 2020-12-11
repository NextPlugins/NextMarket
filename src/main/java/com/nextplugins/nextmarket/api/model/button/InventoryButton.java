package com.nextplugins.nextmarket.api.model.button;

import lombok.Builder;
import lombok.Data;
import org.bukkit.material.MaterialData;

import java.util.List;

/**
 * @author Henry Fábio
 */
@Builder
@Data
public final class InventoryButton {

    private final MaterialData materialData;

    private final String displayName;
    private final List<String> lore;

    private final int inventorySlot;

}
