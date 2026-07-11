package com.nextplugins.nextmarket.inventory.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Lightweight cross-version menu based only on the Bukkit inventory API (1.8+).
 */
public abstract class MarketMenu implements InventoryHolder {

    private final String title;
    private final int size;
    private final Map<Integer, Consumer<InventoryClickEvent>> clickHandlers = new HashMap<Integer, Consumer<InventoryClickEvent>>();

    private Inventory inventory;
    private Player viewer;

    protected MarketMenu(String title, int size) {
        this.title = truncateTitle(title);
        this.size = normalizeSize(size);
    }

    private static int normalizeSize(int size) {
        if (size < 9) return 9;
        if (size > 54) return 54;
        int rows = (int) Math.ceil(size / 9.0);
        return rows * 9;
    }

    private static String truncateTitle(String title) {
        if (title == null) return "";
        // 1.8 inventory titles are limited to 32 characters
        return title.length() > 32 ? title.substring(0, 32) : title;
    }

    public final void open(Player player) {
        this.viewer = player;
        this.clickHandlers.clear();
        this.inventory = Bukkit.createInventory(this, size, this.title);
        render(player);
        player.openInventory(inventory);
    }

    /** Rebuild contents while keeping the inventory open. */
    public final void refresh() {
        if (viewer == null || inventory == null) return;
        clickHandlers.clear();
        inventory.clear();
        render(viewer);
    }

    protected abstract void render(Player player);

    protected void setItem(int slot, ItemStack itemStack) {
        setItem(slot, itemStack, null);
    }

    protected void setItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
        if (inventory == null || slot < 0 || slot >= inventory.getSize()) return;
        inventory.setItem(slot, itemStack);
        if (handler != null) {
            clickHandlers.put(slot, handler);
        } else {
            clickHandlers.remove(slot);
        }
    }

    protected void handleClick(InventoryClickEvent event) {
        Consumer<InventoryClickEvent> handler = clickHandlers.get(event.getRawSlot());
        if (handler != null) {
            handler.accept(event);
        }
    }

    protected Player getViewer() {
        return viewer;
    }

    protected int getSize() {
        return size;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
