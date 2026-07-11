package com.nextplugins.nextmarket.compat;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Method;

/**
 * Main-hand helpers that work on 1.8 ({@code getItemInHand}) and 1.9+ ({@code getItemInMainHand}).
 */
public final class PlayerHands {

    private static final Method GET_MAIN_HAND;
    private static final Method SET_MAIN_HAND;

    static {
        Method get = null;
        Method set = null;
        try {
            get = PlayerInventory.class.getMethod("getItemInMainHand");
            set = PlayerInventory.class.getMethod("setItemInMainHand", ItemStack.class);
        } catch (NoSuchMethodException ignored) {
            // 1.8
        }
        GET_MAIN_HAND = get;
        SET_MAIN_HAND = set;
    }

    private PlayerHands() {
    }

    public static ItemStack getMainHand(Player player) {
        PlayerInventory inventory = player.getInventory();
        if (GET_MAIN_HAND != null) {
            try {
                return (ItemStack) GET_MAIN_HAND.invoke(inventory);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return player.getItemInHand();
    }

    public static void setMainHand(Player player, ItemStack itemStack) {
        PlayerInventory inventory = player.getInventory();
        if (SET_MAIN_HAND != null) {
            try {
                SET_MAIN_HAND.invoke(inventory, itemStack);
                return;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        player.setItemInHand(itemStack);
    }

    public static void clearMainHand(Player player) {
        setMainHand(player, null);
    }

}
