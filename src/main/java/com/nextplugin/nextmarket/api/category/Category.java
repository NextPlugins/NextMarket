package com.nextplugin.nextmarket.api.category;

import com.nextplugin.nextmarket.api.category.icon.CategoryIcon;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;

import java.util.List;
import java.util.Set;

@Builder
@Data
public final class Category {

    private final String id;
    private final String displayName;
    private final String description;

    private final CategoryIcon icon;
    private final Set<Material> allowedMaterials;

}
