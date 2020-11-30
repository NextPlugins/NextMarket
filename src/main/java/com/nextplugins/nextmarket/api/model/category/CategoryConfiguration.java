package com.nextplugins.nextmarket.api.model.category;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;

import java.util.List;

/**
 * @author Henry Fábio
 */
@Builder
@Data
public final class CategoryConfiguration {

    private final String inventoryTitle;
    private final List<Material> materials;

}
