package com.nextplugin.nextmarket.api.button;

import com.nextplugin.nextmarket.api.item.MenuIcon;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Builder
@Data
public final class Button {

    private final String displayName, menu;
    private final MenuIcon icon;
    private final List<String> description;

}
