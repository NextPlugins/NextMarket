package com.nextplugins.nextmarket.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.logging.Level;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtils {

    @Nullable
    public static TextComponent sendItemTooltipMessage(String message, ItemStack item) {
        String itemJson = convertItemStackToJson(item);
        if (itemJson == null) return null;

        BaseComponent[] hoverEventComponents = {new TextComponent(itemJson)};
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        TextComponent component = new TextComponent(message);
        component.setHoverEvent(event);

        return component;
    }

    @Nullable
    private static String convertItemStackToJson(ItemStack itemStack) {
        Class<?> craftItemStackClazz = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
        Method asNMSCopyMethod = ReflectionUtils.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);
        Class<?> nmsItemStackClazz = ReflectionUtils.getNMSClass("ItemStack");

        Class<?> nbtTagCompoundClazz = ReflectionUtils.getNMSClass("NBTTagCompound");
        if (nbtTagCompoundClazz == null) return null;

        Method saveNmsItemStackMethod = ReflectionUtils.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);
        Object itemAsJsonObject;

        try {
            Object nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            Object nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
            return null;
        }
        return itemAsJsonObject.toString();
    }

}
