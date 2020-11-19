package com.nextplugin.nextmarket.inventory;

import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.global.GlobalInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.IViewer;
import com.nextplugin.nextmarket.api.button.Button;
import com.nextplugin.nextmarket.api.item.MenuIcon;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.configuration.InventoryConfiguration;
import com.nextplugin.nextmarket.manager.ButtonManager;
import com.nextplugin.nextmarket.manager.CategoryManager;
import com.nextplugin.nextmarket.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public final class MarketInventory extends GlobalInventory {

    private final CategoryManager categoryManager;
    private final ButtonManager buttonManager;
    private final MarketCache marketCache;

    public MarketInventory(CategoryManager categoryManager, ButtonManager buttonManager, MarketCache marketCache) {

        super("nextmarket.market",
                InventoryConfiguration.get(InventoryConfiguration::mainInventoryTitle),
                InventoryLine.valueOf(InventoryConfiguration.get(InventoryConfiguration::mainInventoryLines))
        );

        this.categoryManager = categoryManager;
        this.buttonManager = buttonManager;
        this.marketCache = marketCache;
    }

    @Override
    protected void onCreate(InventoryEditor editor) {

        setButtonsInMenu(editor);
        setItemsInMenu(editor);

    }

    @Override
    protected void onOpen(IViewer viewer, InventoryEditor editor) {

        this.updateInventory(viewer);

    }

    @Override
    protected void onUpdate(InventoryEditor editor) {
    }

    public void setItemsInMenu(InventoryEditor editor){
        categoryManager.getCategoryMap().forEach((s, category) -> {

            MenuIcon icon = category.getIcon();

            List<String> lore = new ArrayList<>();

            List<MarketItem> collect = new ArrayList<>();
            for (MarketItem marketItem : marketCache.getCache()) {
                if (category.getAllowedMaterials().contains(marketItem.getItemStack().getType())
                        && !marketItem.isExpired()
                        && marketItem.getDestinationId() == null) {
                    collect.add(marketItem);
                }

            }

            int size = collect.size();
            String suffix = size > 1 ? " itens" : " item";

            for (String string : category.getDescription()) {
                lore.add(ChatColor.translateAlternateColorCodes('&', string.replace("%amount%", size + suffix)));
            }

            ItemStack itemStack = new ItemBuilder(icon.getItemStack().getType())
                    .amount(Math.min(size, 64))
                    .durability(icon.getItemStack().getDurability())
                    .name(category.getDisplayName()
                            .replace("&", "§")
                            .replace("%amount%", size + suffix))
                    .lore(lore)
                    .flag(ItemFlag.values())
                    .build();

            editor.setItem(icon.getPosition(), new InventoryItem(itemStack).addDefaultCallback(click -> {

                CategoryInventory categoryInventory = new CategoryInventory(marketCache, categoryManager);

                categoryInventory.openInventory(click.getPlayer(), viewerProperty -> viewerProperty.setProperty("category", category.getId()));

            }));

            collect.forEach(marketItem -> System.out.println(marketItem.toString()));

        });
    }

    public void setButtonsInMenu(InventoryEditor editor){
        List<Button> buttons = new ArrayList<>();
        buttons.add(buttonManager.getButtonMap().get("pessoal"));
        buttons.add(buttonManager.getButtonMap().get("anunciados"));

        for (Button button : buttons) {
            List<String> lore = new ArrayList<>();

            for (String string : button.getDescription()) lore.add(string.replace("&", "§"));

            ItemStack item = ItemBuilder.create(button.getIcon().getItemStack().getType())
                    .amount(1)
                    .durability(button.getIcon().getItemStack().getDurability())
                    .name(ChatColor.translateAlternateColorCodes('&', button.getDisplayName()))
                    .lore(lore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()))
                    .flag(ItemFlag.values())
                    .build();

            editor.setItem(button.getIcon().getPosition(), new InventoryItem(item)
                    .addDefaultCallback(click -> click.getPlayer().performCommand("mercado " + button.getMenu())));
        }
    }

}
