package com.nextplugin.nextmarket.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TextUtil {

    private final TextComponent component;
    private final List<BaseComponent> baseComponent;
    private TextComponent textComponent;

    public TextUtil() {
        this.baseComponent = new ArrayList<>();
        this.component = new TextComponent("");
    }

    public TextUtil(String prefix) {
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
        final Class<?> craftItemStackClazz = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
        final Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);
        final Class<?> nmsItemStackClazz = ReflectionUtil.getNMSClass("ItemStack");
        final Class<?> nbtTagCompoundClazz = ReflectionUtil.getNMSClass("NBTTagCompound");
        final Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);
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

    public TextUtil prefix(String prefix) {
        this.text(prefix);
        return this;
    }

    public TextUtil text(String text) {
        this.textComponent = new TextComponent(TextComponent.fromLegacyText(text));
        return this;
    }

    public TextUtil hoverText(String text) {
        BaseComponent[] hover = {new TextComponent(text)};
        this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
        return this;
    }

    public TextUtil clickOpenURL(String url) {
        this.textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, (url.startsWith("http") ? "" : "https://") + url));
        return this;
    }

    public TextUtil clickRunCommand(String command) {
        this.textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }

    public TextUtil clickSuggest(String suggest) {
        this.textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        return this;
    }

    public TextUtil next() {
        if (this.textComponent == null) {
            return this;
        }
        this.baseComponent.add(this.textComponent);
        return this;
    }

    public void send(Player player) {
        this.component.setExtra(this.baseComponent);
        player.spigot().sendMessage(this.component);
    }

    public void send(CommandSender sender) {
        this.component.setExtra(this.baseComponent);
        sender.sendMessage(this.component.getText());
    }

    public void sendEveryOne() {
        this.component.setExtra(this.baseComponent);
        for (Player everyone : Bukkit.getOnlinePlayers()) {
            everyone.spigot().sendMessage(this.component);
        }
    }
}
