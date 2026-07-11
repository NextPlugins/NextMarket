package com.nextplugins.nextmarket.inventory.menu;

import com.nextplugins.nextmarket.compat.Items;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Paginated menu: content occupies all slots except the last row (controls).
 * Default control slots (for 6-row / 54-slot inventories): previous=45, back=48, update=49, next=53.
 */
public abstract class PagedMarketMenu extends MarketMenu {

    private int page;
    private final List<PageItem> pageItems = new ArrayList<PageItem>();

    protected PagedMarketMenu(String title, int size) {
        super(title, size);
    }

    protected abstract void collectItems(Player player, List<PageItem> items);

    protected void configureControls(Player player) {
        int lastRowStart = getSize() - 9;

        // Only show navigation arrows when the target page exists.
        if (page > 0) {
            setItem(lastRowStart, createPreviousItem(), event -> {
                page--;
                refresh();
            });
        }

        setItem(lastRowStart + 3, createBackItem(), this::onBack);
        setItem(lastRowStart + 4, createUpdateItem(player), event -> {
            page = 0;
            refresh();
        });

        if (page < getMaxPage()) {
            setItem(lastRowStart + 8, createNextItem(), event -> {
                page++;
                refresh();
            });
        }
    }

    protected void onBack(InventoryClickEvent event) {
        event.getWhoClicked().closeInventory();
    }

    protected ItemStack createPreviousItem() {
        return Items.named(Items.create("ARROW"), ChatColor.YELLOW + "Página anterior");
    }

    protected ItemStack createNextItem() {
        return Items.named(Items.create("ARROW"), ChatColor.YELLOW + "Próxima página");
    }

    protected ItemStack createBackItem() {
        return Items.named(Items.create("ARROW"), ChatColor.RED + "Voltar");
    }

    protected ItemStack createUpdateItem(Player player) {
        return Items.named(Items.create("EMERALD"), ChatColor.GREEN + "Atualizar");
    }

    @Override
    protected final void render(Player player) {
        pageItems.clear();
        collectItems(player, pageItems);

        int contentSlots = Math.max(0, getSize() - 9);
        int from = page * contentSlots;
        if (from >= pageItems.size() && page > 0) {
            page = getMaxPage();
            from = page * contentSlots;
        }

        int to = Math.min(from + contentSlots, pageItems.size());
        for (int i = from; i < to; i++) {
            PageItem pageItem = pageItems.get(i);
            int slot = i - from;
            setItem(slot, pageItem.itemStack, pageItem.handler);
        }

        configureControls(player);
    }

    protected int getPage() {
        return page;
    }

    protected void setPage(int page) {
        this.page = Math.max(0, page);
    }

    protected int getMaxPage() {
        int contentSlots = Math.max(1, getSize() - 9);
        if (pageItems.isEmpty()) return 0;
        return (pageItems.size() - 1) / contentSlots;
    }

    public static final class PageItem {
        private final ItemStack itemStack;
        private final Consumer<InventoryClickEvent> handler;

        public PageItem(ItemStack itemStack, Consumer<InventoryClickEvent> handler) {
            this.itemStack = itemStack;
            this.handler = handler;
        }

        public PageItem(ItemStack itemStack) {
            this(itemStack, null);
        }
    }

    protected static List<PageItem> emptyPageItems() {
        return Collections.emptyList();
    }

}
