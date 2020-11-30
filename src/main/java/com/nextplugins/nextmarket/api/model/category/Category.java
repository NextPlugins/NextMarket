package com.nextplugins.nextmarket.api.model.category;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Henry Fábio
 */
@EqualsAndHashCode(of = "id")
@Builder
@Data
public final class Category {

    private final String id;

    private final String displayName;
    private final List<String> description;

    private final CategoryIcon icon;
    private final CategoryConfiguration configuration;

}
