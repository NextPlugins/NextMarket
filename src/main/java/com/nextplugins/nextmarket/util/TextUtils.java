package com.nextplugins.nextmarket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Chat helpers. Item-hover uses a best-effort path and falls back to plain text
 * when the server cannot serialize the item (common on remapped Paper builds).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtils {

    @Nullable
    public static TextComponent sendItemTooltipMessage(String message, ItemStack item) {
        if (message == null) return null;

        TextComponent component = new TextComponent(message);
        String itemJson = convertItemStackToJson(item);
        if (itemJson == null) {
            return component;
        }

        try {
            BaseComponent[] hoverEventComponents = {new TextComponent(itemJson)};
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);
            component.setHoverEvent(event);
        } catch (Throwable ignored) {
            // SHOW_ITEM payload formats differ across versions — plain text is fine.
        }

        return component;
    }

    @Nullable
    private static String convertItemStackToJson(ItemStack itemStack) {
        if (itemStack == null) return null;

        // Prefer NBT-API when available (shaded, multi-version).
        try {
            de.tr7zw.changeme.nbtapi.NBTItem nbtItem = new de.tr7zw.changeme.nbtapi.NBTItem(itemStack);
            String nbt = nbtItem.toString();
            if (nbt != null && !nbt.isEmpty()) {
                String type = itemStack.getType().name().toLowerCase();
                int count = itemStack.getAmount();
                // Legacy hover format used by many 1.8–1.12 clients
                return "{id:\"" + type + "\",Count:" + count + "b,tag:" + nbt + "}";
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

}
