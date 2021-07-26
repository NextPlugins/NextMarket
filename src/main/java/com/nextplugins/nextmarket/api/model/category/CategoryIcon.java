package com.nextplugins.nextmarket.api.model.category;

import com.nextplugins.nextmarket.api.model.product.MaterialData;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public final class CategoryIcon {

    private final MaterialData materialData;
    private final boolean enchant;
    private final int inventorySlot;

}
