package com.nextplugins.nextmarket.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TextUtils {

    private final TextComponent component;
    private final List<BaseComponent> baseComponent;
    private TextComponent textComponent;

    public TextUtils() {
        this.baseComponent = new ArrayList<>();
        this.component = new TextComponent("");
    }

    public TextUtils(String prefix) {
        this.baseComponent = new ArrayList<>();
        this.component = new TextComponent(prefix);
    }

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

    public TextUtils prefix(String prefix) {
        this.text(prefix);
        return this;
    }

    public TextUtils text(String text) {
        this.textComponent = new TextComponent(TextComponent.fromLegacyText(text));
        return this;
    }

    public TextUtils hoverText(String text) {
        BaseComponent[] hover = {new TextComponent(text)};
        this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
        return this;
    }

    public TextUtils clickOpenURL(String url) {
        this.textComponent.setClickEvent(new ClickEvent(
                ClickEvent.Action.OPEN_URL, (url.startsWith("http") ? "" : "https://") + url)
        );
        return this;
    }

    public TextUtils clickRunCommand(String command) {
        this.textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }

    public TextUtils clickSuggest(String suggest) {
        this.textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        return this;
    }

    public TextUtils next() {
        if (this.textComponent == null) {
            return this;
        }
        this.baseComponent.add(this.textComponent);
        return this;
    }

}
