package com.nextplugins.nextmarket.inventory;

import com.nextplugins.nextmarket.compat.Items;
import com.nextplugins.nextmarket.inventory.menu.MarketMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public final class ConfirmationInventory {

    public void openConfirmation(Player player, String description, Runnable confirm, Runnable decline, ItemStack itemStack) {
        final String safeDescription = description == null ? "Confirmação" : description;
        final boolean hasPreview = itemStack != null;
        int size = (hasPreview ? 4 : 3) * 9;
        String title = ("§8" + safeDescription);
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        new MarketMenu(title, size) {
            @Override
            protected void render(Player viewer) {
                int increment = hasPreview ? 9 : 0;

                if (hasPreview) {
                    setItem(13, itemStack.clone());
                }

                ItemStack confirmItem = Items.named(
                        Items.create("LIME_TERRACOTTA"),
                        "§aConfirmar",
                        Arrays.asList(
                                "§7Clique para confirmar esta ação!",
                                "§c§lOBS: §cEsta opção é irreversível!"
                        )
                );
                // Fallback for 1.8 stained clay
                if (confirmItem.getType().name().equals("STONE") || confirmItem.getType().name().contains("BARRIER")) {
                    confirmItem = Items.named(
                            Items.create("STAINED_CLAY", 13),
                            "§aConfirmar",
                            Arrays.asList(
                                    "§7Clique para confirmar esta ação!",
                                    "§c§lOBS: §cEsta opção é irreversível!"
                            )
                    );
                }

                ItemStack declineItem = Items.named(
                        Items.create("RED_TERRACOTTA"),
                        "§cCancelar",
                        Arrays.asList(
                                "§7Clique para cancelar esta ação!",
                                "§c§lOBS: §cEsta opção é irreversível!"
                        )
                );
                if (declineItem.getType().name().equals("STONE") || declineItem.getType().name().contains("BARRIER")) {
                    declineItem = Items.named(
                            Items.create("STAINED_CLAY", 14),
                            "§cCancelar",
                            Arrays.asList(
                                    "§7Clique para cancelar esta ação!",
                                    "§c§lOBS: §cEsta opção é irreversível!"
                            )
                    );
                }

                setItem(11 + increment, confirmItem, event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    if (confirm != null) confirm.run();
                });

                setItem(15 + increment, declineItem, event -> {
                    Player clicker = (Player) event.getWhoClicked();
                    clicker.closeInventory();
                    if (decline != null) decline.run();
                });
            }
        }.open(player);
    }

    public void openConfirmation(Player player, String description, Runnable confirm, ItemStack itemStack) {
        openConfirmation(player, description, confirm, null, itemStack);
    }

    public void openConfirmation(Player player, String description, Runnable confirm) {
        openConfirmation(player, description, confirm, null, null);
    }

}
