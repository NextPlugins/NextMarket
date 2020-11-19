package com.nextplugin.nextmarket.listener;

import com.nextplugin.nextmarket.api.event.*;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.cache.PlayerAnnouncementDelay;
import com.nextplugin.nextmarket.configuration.ConfigValue;
import com.nextplugin.nextmarket.hook.VaultHook;
import com.nextplugin.nextmarket.util.NumberUtil;
import com.nextplugin.nextmarket.util.TextUtil;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class MarketEventListener implements Listener {

    Economy economy = new VaultHook().getEconomy();

    private final MarketCache marketCache;
    private final PlayerAnnouncementDelay playerCache;

    public MarketEventListener(MarketCache marketCache, PlayerAnnouncementDelay playerCache) {
        this.marketCache = marketCache;
        this.playerCache = playerCache;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBuyingItem(MarketItemBuyingEvent event) {

        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        MarketItem marketItem = event.getMarketItem();

        double balance = economy.getBalance(player);
        if (balance < marketItem.getPrice()) {

            String insufficientMoney = ConfigValue.get(ConfigValue::insufficientMoneyMessage)
                    .replace("%difference%", String.valueOf(marketItem.getPrice() - balance));

            player.sendMessage(insufficientMoney);
            return;
        }

        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() == -1) {
            player.getPlayer().sendMessage(ConfigValue.get(ConfigValue::fullInventoryMessage));
            return;
        }

        Bukkit.getPluginManager().callEvent(new MarketItemBuyedEvent(player, marketItem));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBuyItem(MarketItemBuyedEvent event) {

        Player player = event.getPlayer();
        MarketItem marketItem = event.getMarketItem();
        ItemStack itemStack = marketItem.getItemStack().clone();

        String boughtAnItem = ConfigValue.get(ConfigValue::boughtAnItemMessage);
        player.sendMessage(boughtAnItem);

        player.getInventory().addItem(itemStack);

        economy.withdrawPlayer(player, marketItem.getPrice());
        marketCache.removeItem(marketItem);

        Bukkit.getPluginManager().callEvent(new MarketItemSellEvent(marketItem.getSeller().getPlayer(), marketItem));

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreateAnnounce(MarketItemCreateEvent event) {

        if (event.isCancelled()) return;

        MarketItem marketItem = event.getMarketItem();
        Player player = event.getPlayer();

        double price = marketItem.getPrice();

        String priceSimpleFormatted = "%price%";
        String priceLetterFormatted = "%priceFormatted%";

        if (marketItem.getDestinationId() != null) {

            Player destination = marketItem.getDestination().getPlayer();

            marketCache.addItem(marketItem, true);
            player.setItemInHand(null);
            player.updateInventory();
            player.sendMessage(ConfigValue.get(ConfigValue::announcedAItemInPersonalMarket)
                    .replace(priceSimpleFormatted, NumberUtil.formatNumber(price))
                    .replace(priceLetterFormatted, NumberUtil.letterFormat(price))
                    .replace("%player%", destination.getName()));

        } else {

            player.setItemInHand(null);
            player.updateInventory();
            player.sendMessage(ConfigValue.get(ConfigValue::announcedAItemMessage)
                    .replace(priceSimpleFormatted, NumberUtil.formatNumber(price))
                    .replace(priceLetterFormatted, NumberUtil.letterFormat(price)));

            String announcementMessage = ConfigValue.get(ConfigValue::announcementMessage)
                    .replace(priceSimpleFormatted, NumberUtil.formatNumber(price))
                    .replace(priceLetterFormatted, NumberUtil.letterFormat(price))
                    .replace("%player%", player.getName());

            TextComponent text = TextUtil.sendItemTooltipMessage(announcementMessage, marketItem.getItemStack());

            if (!playerCache.inDelay(player)) {
                playerCache.putDelay(player, ConfigValue.get(ConfigValue::announcementSecondsDelay));

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.sendMessage("");
                    onlinePlayer.spigot().sendMessage(text);
                    onlinePlayer.sendMessage("");
                }
            }

        }

        marketCache.addItem(marketItem, true);
    }

    @EventHandler
    public void onRemoveAnnounce(MarketItemRemoveEvent event) {

        if (event.isCancelled()) return;

        MarketItem marketItem = event.getMarketItem();
        Player player = event.getPlayer();

        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() == -1) {
            player.sendMessage(ConfigValue.get(ConfigValue::fullInventoryMessage));
            return;
        }

        marketCache.removeItem(marketItem);

        inventory.addItem(marketItem.getItemStack());
        player.sendMessage(ConfigValue.get(ConfigValue::cancelAnSellMessage));
        player.updateInventory();

    }

    @EventHandler
    public void onSellItem(MarketItemSellEvent event) {

        MarketItem marketItem = event.getMarketItem();
        Player player = event.getPlayer();

        if (player != null) {

            String soldAItem = ConfigValue.get(ConfigValue::soldAItemMessage);
            player.sendMessage(soldAItem
                    .replace("%amount%", NumberUtil.formatNumber(marketItem.getPrice())
                            .replace("%amountFormatted%", NumberUtil.letterFormat(marketItem.getPrice()))));

        }

        economy.depositPlayer(player, marketItem.getPrice());

    }

}
