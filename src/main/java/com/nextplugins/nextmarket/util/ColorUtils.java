package com.nextplugins.nextmarket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ColorUtils {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
