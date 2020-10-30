package com.nextplugin.nextmarket.command;

import com.google.inject.Inject;
import com.nextplugin.nextmarket.api.category.Category;
import com.nextplugin.nextmarket.api.event.MarketItemCreateEvent;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.configuration.ConfigValue;
import com.nextplugin.nextmarket.inventory.AnnouncedItemsInventory;
import com.nextplugin.nextmarket.inventory.MarketInventory;
import com.nextplugin.nextmarket.inventory.PrivateMarketInventory;
import com.nextplugin.nextmarket.manager.ButtonManager;
import com.nextplugin.nextmarket.manager.CategoryManager;
import com.nextplugin.nextmarket.util.TimeUtil;
import de.tr7zw.nbtapi.NBTItem;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MarketCommand {

    @Inject private MarketCache marketCache;
    @Inject private CategoryManager categoryManager;
    @Inject private ButtonManager buttonManager;

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
        messages.forEach(player::sendMessage);

    }

    @Command(name = "mercado.ver")
    public void viewMarket(Context<Player> context) {

        Player player = context.getSender();

        MarketInventory marketInventory = new MarketInventory(categoryManager, buttonManager, marketCache);
        marketInventory.openInventory(player);

        player.sendMessage(ConfigValue.get(ConfigValue::openingInventoryMessage));

    }

    @Command(name = "mercado.pessoal")
    public void viewPersonalMarket(Context<Player> context) {

        PrivateMarketInventory privateMarketInventory = new PrivateMarketInventory(marketCache);
        privateMarketInventory.openInventory(context.getSender());

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

        final Collection<Category> categories = categoryManager.getCategoryMap().values();

        List<Material> allowedMaterials = new ArrayList<>();
        for (Category category : categories) {
            allowedMaterials.addAll(category.getAllowedMaterials());
        }

        if (itemInMainHand == null ||
                itemInMainHand.getType() == Material.AIR ||
                !allowedMaterials.contains(itemInMainHand.getType())) {

            player.sendMessage(ConfigValue.get(ConfigValue::invalidItemMessage));
            return;

        }

        NBTItem nbtItem = new NBTItem(itemInMainHand);

        long sellTiming = nbtItem.getLong("sellTiming");
        long currentTime = System.currentTimeMillis();

        if (!(sellTiming <= currentTime)) {

            long totalTime = sellTiming - currentTime;

            player.sendMessage(ConfigValue.get(ConfigValue::inSellTiming)
                    .replace("%timeRemaining%", TimeUtil.format(totalTime)));

            System.out.println(totalTime);

        } else {

            nbtItem.clearCustomNBT();

            int itemsInMarket = (int) marketCache.getMarketCache()
                    .stream()
                    .filter(marketItem -> marketItem.getSellerId().equals(player.getUniqueId()))
                    .count();

            int playerLimit = 0;

            ConfigurationSection limitConfiguration = ConfigValue.get(ConfigValue::sellLimit);
            for (String permission : limitConfiguration.getKeys(false)) {

                if (player.hasPermission("nextmarket." + permission)) {

                    playerLimit = limitConfiguration.getInt(permission);
                }
            }

            if (playerLimit == 0) {
                player.sendMessage(ConfigValue.get(ConfigValue::noPermissionMessage));
                return;
            }

            if (itemsInMarket == playerLimit) {
                player.sendMessage(ConfigValue.get(ConfigValue::outOfBoundsMessage)
                        .replace("%limit%", String.valueOf(playerLimit)));
                return;
            }

            MarketItem marketItem;
            if (target != null) {
                marketItem = new MarketItem(
                        player.getUniqueId(),
                        itemInMainHand,
                        value,
                        new Date(),
                        target.getUniqueId()
                );
            } else {
                marketItem = new MarketItem(
                        player.getUniqueId(),
                        itemInMainHand,
                        value,
                        new Date(),
                        null
                );
            }

            Bukkit.getServer().getPluginManager().callEvent(new MarketItemCreateEvent(player, marketItem));

        }

    }

    @Command(name = "mercado.anunciados")
    public void viewMarketAnnouncedItems(Context<Player> context) {

        Player player = context.getSender();

        AnnouncedItemsInventory announcedItemsInventory = new AnnouncedItemsInventory(marketCache);
        announcedItemsInventory.openInventory(player);

    }

}
