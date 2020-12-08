package com.nextplugins.nextmarket.api.model.category;

import lombok.Builder;
import lombok.Data;
import org.bukkit.material.MaterialData;

/**
 * @author Henry Fábio
 */
@Builder
@Data
public final class CategoryIcon {

    private final MaterialData materialData;
    private final boolean enchant;
    private final int inventorySlot;

}
