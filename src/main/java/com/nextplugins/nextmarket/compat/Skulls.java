package com.nextplugins.nextmarket.compat;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Player head helpers for SKULL_ITEM (1.8–1.12) and PLAYER_HEAD (1.13+).
 */
public final class Skulls {

    private static final Method SET_OWNING_PLAYER;

    static {
        Method method = null;
        try {
            method = SkullMeta.class.getMethod("setOwningPlayer", org.bukkit.OfflinePlayer.class);
        } catch (NoSuchMethodException ignored) {
            // pre-1.12.1
        }
        SET_OWNING_PLAYER = method;
    }

    private Skulls() {
    }

    public static ItemStack createPlayerHead() {
        Optional<XMaterial> head = XMaterial.matchXMaterial("PLAYER_HEAD");
        if (head.isPresent() && head.get().isSupported()) {
            ItemStack stack = head.get().parseItem();
            if (stack != null) return stack;
        }

        Optional<XMaterial> skull = XMaterial.matchXMaterial("SKULL_ITEM");
        if (skull.isPresent() && skull.get().isSupported()) {
            ItemStack stack = skull.get().parseItem();
            if (stack != null) return stack;
        }

        // Legacy data value 3 = player skull
        return Items.create("SKULL_ITEM", 3);
    }

    public static ItemStack withOwner(ItemStack itemStack, String playerName) {
        if (itemStack == null || playerName == null) return itemStack;

        ItemMeta meta = itemStack.getItemMeta();
        if (!(meta instanceof SkullMeta)) {
            // Material might not be a skull on this version — try converting
            ItemStack head = createPlayerHead();
            head.setItemMeta(itemStack.getItemMeta());
            meta = head.getItemMeta();
            itemStack = head;
        }

        if (!(meta instanceof SkullMeta)) {
            return itemStack;
        }

        SkullMeta skullMeta = (SkullMeta) meta;
        if (SET_OWNING_PLAYER != null) {
            try {
                org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(playerName);
                SET_OWNING_PLAYER.invoke(skullMeta, offlinePlayer);
            } catch (Throwable t) {
                skullMeta.setOwner(playerName);
            }
        } else {
            skullMeta.setOwner(playerName);
        }

        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    public static boolean isSkull(ItemStack itemStack) {
        if (itemStack == null) return false;
        String name = itemStack.getType().name();
        return name.contains("SKULL") || name.contains("HEAD") && name.contains("PLAYER");
    }

}
