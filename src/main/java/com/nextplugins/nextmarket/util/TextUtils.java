package com.nextplugins.nextmarket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.logging.Level;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtils {

    public static TextComponent sendItemTooltipMessage(final String message, final ItemStack item) {
        final String itemJson = convertItemStackToJson(item);
        final BaseComponent[] hoverEventComponents = {new TextComponent(itemJson)};
        final HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);
        final TextComponent component = new TextComponent(message);
        component.setHoverEvent(event);
        return component;
    }

    private static String convertItemStackToJson(final ItemStack itemStack) {
        final Class<?> craftItemStackClazz = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
        final Method asNMSCopyMethod = ReflectionUtils.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);
        final Class<?> nmsItemStackClazz = ReflectionUtils.getNMSClass("ItemStack");
        final Class<?> nbtTagCompoundClazz = ReflectionUtils.getNMSClass("NBTTagCompound");
        final Method saveNmsItemStackMethod = ReflectionUtils.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);
        Object itemAsJsonObject;
        try {
            final Object nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            final Object nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
            return null;
        }
        return itemAsJsonObject.toString();
    }

}
