package com.nextplugins.nextmarket.api.model.category;

import com.nextplugins.nextmarket.api.model.product.MaterialData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public final class CategoryConfiguration {

    private final String inventoryTitle;
    private final List<MaterialData> materials;

}
