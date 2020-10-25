package com.nextplugin.nextmarket.listeners;

import com.nextplugin.nextmarket.api.event.MarketItemBuyEvent;
import com.nextplugin.nextmarket.api.event.MarketItemCreateEvent;
import com.nextplugin.nextmarket.api.item.MarketItem;
import com.nextplugin.nextmarket.cache.MarketCache;
import com.nextplugin.nextmarket.configuration.ConfigValue;
import com.nextplugin.nextmarket.hook.VaultHook;
import com.nextplugin.nextmarket.sql.MarketDAO;
import com.nextplugin.nextmarket.util.NumberUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;

import javax.inject.Inject;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class MarketEvents implements Listener {

    @Inject private MarketDAO marketDAO;
    @Inject private MarketCache marketCache;
    @Inject private VaultHook vaultHook;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBuyEvent(MarketItemBuyEvent event) {
        if (event.isCancelled()) return;

        MarketItem marketItem = event.getMarketItem();
        Player player = event.getPlayer();
        Player seller = marketItem.getSeller().getPlayer();
        Economy economy = vaultHook.getEconomy();

        double balance = economy.getBalance(player);

        if(balance < marketItem.getPrice()){

            String insufficientMoney = ConfigValue.get(ConfigValue::insufficientMoneyMessage)
                    .replace("%difference%", String.valueOf(balance - marketItem.getPrice()));

            player.sendMessage(insufficientMoney);
            return;
        }

        marketCache.removeItem(marketItem);

        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() == -1) {
            player.getPlayer().sendMessage(ConfigValue.get(ConfigValue::fullInventoryMessage));
            return;
        }

        player.getInventory().addItem(marketItem.getItemStack());

        String boughtAnItem = ConfigValue.get(ConfigValue::boughtAnItemMessage);
        player.sendMessage(boughtAnItem);

        String soldAItem = ConfigValue.get(ConfigValue::soldAItemMessage);
        seller.sendMessage(soldAItem.replace("%amount%", NumberUtil.letterFormat(marketItem.getPrice())));

        economy.bankWithdraw(player.getName(), marketItem.getPrice());
        economy.bankDeposit(seller.getName(), marketItem.getPrice());

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreateAnnounce(MarketItemCreateEvent event) {
        // TODO
    }

}
