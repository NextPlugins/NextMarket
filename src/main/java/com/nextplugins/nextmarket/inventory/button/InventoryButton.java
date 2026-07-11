package com.nextplugins.nextmarket.inventory.button;

import com.nextplugins.nextmarket.api.model.product.MaterialData;
import com.nextplugins.nextmarket.compat.Items;
import com.nextplugins.nextmarket.compat.Skulls;
import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Builder
@Data
public final class InventoryButton {

    private final MaterialData materialData;

    private final String displayName;
    private final List<String> lore;

    private final int inventorySlot;

    private ItemStack itemStack;

    public ItemStack getItemStack() {
        if (this.itemStack == null) {
            this.itemStack = materialData != null
                    ? materialData.toItemStack(1)
                    : Items.fallbackBarrier();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (this.displayName != null) {
                    itemMeta.setDisplayName(this.displayName);
                }
                if (this.lore != null) {
                    itemMeta.setLore(this.lore);
                }
                Items.applySafeFlags(itemMeta);
                this.itemStack.setItemMeta(itemMeta);
            }
        }
        return this.itemStack;
    }

    public ItemStack getSkullItemStack(String playerName) {
        ItemStack base = this.getItemStack().clone();
        ItemStack head = Skulls.createPlayerHead();

        ItemMeta baseMeta = base.getItemMeta();
        if (baseMeta != null) {
            head.setItemMeta(baseMeta);
        }

        return Skulls.withOwner(head, playerName);
    }

}
