package com.nextplugin.nextmarket.command;

import com.google.inject.Inject;
import com.henryfabio.inventoryapi.editor.InventoryEditor;
import com.henryfabio.inventoryapi.enums.InventoryLine;
import com.henryfabio.inventoryapi.inventory.paged.PagedInventory;
import com.henryfabio.inventoryapi.item.InventoryItem;
import com.henryfabio.inventoryapi.viewer.paged.PagedViewer;
import com.nextplugin.nextmarket.api.event.MarketItemCreateEvent;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.configuration.ConfigValue;
import com.nextplugin.nextmarket.inventory.ExpireItemsInventory;
import com.nextplugin.nextmarket.inventory.MarketInventory;
import com.nextplugin.nextmarket.manager.CategoryManager;
import com.nextplugin.nextmarket.sql.MarketDAO;
import com.nextplugin.nextmarket.util.NumberUtil;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MarketCommand {

    @Inject private MarketDAO marketDAO;
    @Inject private CategoryManager categoryManager;


    @Command(
            name = "mercado",
            aliases = {"market"},
            permission = "nextmarket.use",
            description = "Comando principal do sistema de mercado.",
            target = CommandTarget.PLAYER
    )
    public void marketCommand(Context<Player> context) {
        Player player = context.getSender();

        List<String> messages = ConfigValue.get(ConfigValue::commandMessage);
        for (String message : messages) {
            player.sendMessage(message);
        }

    }

    @Command(name = "mercado.ver")
    public void viewMarket(Context<Player> context) {

        Player player = context.getSender();

        MarketInventory marketInventory = new MarketInventory(categoryManager, marketDAO);

        marketInventory.openInventory(player);

    }

    @Command(name = "mercado.pessoal")
    public void viewPersonalMarket(Context<Player> context) {

    }

    @Command(name = "mercado.vender")
    public void announceItemOnMarket(Context<Player> context, double value, @Optional Player target) {

        Player player = context.getSender();

        double maxValue = ConfigValue.get(ConfigValue::maximumAnnouncementValue);
        double minValue = ConfigValue.get(ConfigValue::minimumAnnouncementValue);

        if (maxValue != -1 && value > maxValue) {
            player.sendMessage(ConfigValue.get(ConfigValue::maximumValueReachedMessage));
            return;
        } else if (value < minValue) {
            player.sendMessage(ConfigValue.get(ConfigValue::minimumValueNotReachedMessage));
            return;
        }

        ItemStack itemInMainHand = player.getInventory().getItemInHand();

        if (itemInMainHand == null || itemInMainHand.getType() == Material.AIR) {

            player.sendMessage(ConfigValue.get(ConfigValue::invalidItem));
            return;

        }

        if (target != null) {

            MarketItem marketItem = new MarketItem(
                    player.getUniqueId(),
                    player.getItemInHand(),
                    value,
                    new Date(),
                    target.getUniqueId()
            );

            marketDAO.insertMarketItem(marketItem);
            player.getInventory().remove(itemInMainHand);
            player.sendMessage(ConfigValue.get(ConfigValue::announcedAItemInPersonalMarket)
                    .replace("%amount%", NumberUtil.formatNumber(value))
                    .replace("%player%", target.getName()));

            Bukkit.getServer().getPluginManager().callEvent(new MarketItemCreateEvent(player, marketItem));

        } else {
            MarketItem marketItem = new MarketItem(
                    player.getUniqueId(),
                    player.getItemInHand(),
                    value,
                    new Date(),
                    null
            );

            marketDAO.insertMarketItem(marketItem);
            player.getInventory().remove(itemInMainHand);
            player.sendMessage(ConfigValue.get(ConfigValue::announcedAItemMessage)
                    .replace("%amount%", NumberUtil.formatNumber(value)));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(new String[]{
                        "",
                        ConfigValue.get(ConfigValue::announcementMessage)
                                .replace("%player%", player.getName())
                                .replace("%amount%", NumberUtil.formatNumber(value)),
                        ""
                });
            }

            Bukkit.getServer().getPluginManager().callEvent(new MarketItemCreateEvent(player, marketItem));

        }

    }

    @Command(name = "mercado.expirados")
    public void viewMarketExpiredItems(Context<Player> context) {
        Player player = context.getSender();

        ExpireItemsInventory expireItemsInventory = new ExpireItemsInventory();
        expireItemsInventory.openInventory(player);
    }

    @Command(name = "mercado.anunciados")
    public void viewMarketAnnouncedItems(Context<Player> context) {

        Player player = context.getSender();

        new PagedInventory("announced.inventory", "Mercado", InventoryLine.SIX) {

            @Override
            protected void onCreate(PagedViewer viewer) {

            }

            @Override
            protected void onOpen(PagedViewer viewer, InventoryEditor editor) {

            }

            @Override
            protected void onUpdate(PagedViewer viewer, InventoryEditor editor) {

            }

            @Override
            public List<InventoryItem> getPagesItems(PagedViewer viewer) {

                List<InventoryItem> items = new LinkedList<>();

                List<MarketItem> collect = marketDAO.findAllMarketItemList()
                        .stream()
                        .filter(marketItem -> marketItem.getSellerId().equals(player.getUniqueId()))
                        .collect(Collectors.toList());

                for (MarketItem marketItem : collect) {

                    items.add(new InventoryItem(marketItem.getItemStack()));

                }

                return items;
            }
        }.openInventory(player);
    }

}
