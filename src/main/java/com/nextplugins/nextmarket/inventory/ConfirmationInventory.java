package com.nextplugins.nextmarket.inventory;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.ViewerConfiguration;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.simple.SimpleViewer;
import com.henryfabio.minecraft.inventoryapi.viewer.property.ViewerPropertyMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * @author Henry Fábio
 */
public final class ConfirmationInventory extends SimpleInventory {

    public ConfirmationInventory() {
        super("nextmarket.confirmation", "§8Confirmação", 3 * 9);
    }

    public void openConfirmation(Player player, String description, Runnable confirm, Runnable decline, ItemStack itemStack) {
        openInventory(player, viewer -> {
            ViewerPropertyMap propertyMap = viewer.getPropertyMap();
            propertyMap.set("description", description);
            propertyMap.set("confirm", confirm);
            propertyMap.set("decline", decline);
            propertyMap.set("itemStack", itemStack);
        });
    }

    public void openConfirmation(Player player, String description, Runnable confirm, ItemStack itemStack) {
        openConfirmation(player, description, confirm, null, itemStack);
    }

    public void openConfirmation(Player player, String description, Runnable confirm) {
        openConfirmation(player, description, confirm, null, null);
    }

    @Override
    protected void configureViewer(SimpleViewer viewer) {
        ViewerPropertyMap propertyMap = viewer.getPropertyMap();
        String description = propertyMap.get("description");
        ItemStack itemStack = propertyMap.get("itemStack");

        ViewerConfiguration configuration = viewer.getConfiguration();
        configuration.titleInventory(("§8" + description).substring(0, Math.min(32, description.length() + 2)));
        configuration.inventorySize((itemStack == null ? 3 : 4) * 9);
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        ItemStack itemStack = viewer.getPropertyMap().get("itemStack");
        int increment = itemStack != null ? 9 : 0;

        editor.setItem(11 + increment, confirmInventoryItem());
        editor.setItem(15 + increment, declineInventoryItem());

        if (itemStack != null) {
            editor.setItem(13, InventoryItem.of(itemStack));
        }
    }

    private InventoryItem confirmInventoryItem() {
        ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, 1, (short) 13);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§aConfirmar");
        itemMeta.setLore(Arrays.asList(
                "§7Clique para confirmar esta ação!",
                "§c§lOBS: §cEsta opção é irreversível!"
        ));
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);

        return InventoryItem.of(itemStack)
                .defaultCallback(event -> acceptRunnable(event.getViewer(), "confirm"));
    }

    private InventoryItem declineInventoryItem() {
        ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, 1, (short) 14);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§aCancelar");
        itemMeta.setLore(Arrays.asList(
                "§7Clique para cancelar esta ação!",
                "§c§lOBS: §cEsta opção é irreversível!"
        ));
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);

        return InventoryItem.of(itemStack)
                .defaultCallback(event -> acceptRunnable(event.getViewer(), "decline"));
    }

    private void acceptRunnable(Viewer viewer, String property) {
        Player player = viewer.getPlayer();
        player.closeInventory();

        Runnable runnable = viewer.getPropertyMap().get(property);
        if (runnable != null) runnable.run();
    }

}
