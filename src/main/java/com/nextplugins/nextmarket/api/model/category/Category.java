package com.nextplugins.nextmarket.api.model.category;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString(of = {"id", "displayName"})
@EqualsAndHashCode(of = "id")
@Builder
@Data
public final class Category {

    private final String id;
    private final boolean trashTable;

    private final String displayName;
    private final List<String> description;

    private final CategoryIcon icon;
    private final CategoryConfiguration configuration;

}
