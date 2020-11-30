package com.nextplugins.nextmarket.command;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.configuration.value.MessageValue;
import com.nextplugins.nextmarket.inventory.MarketInventory;
import com.nextplugins.nextmarket.manager.MarketManager;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Henry Fábio
 */
public final class MarketCommand {

    @Inject private MarketManager marketManager;

    @Command(
            name = "market",
            aliases = {"mercado"},
            permission = "nextmarket.use",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void marketCommand(Context<Player> context) {
        context.sendMessage(MessageValue.get(MessageValue::commandMessage).toArray(new String[]{}));
    }

    @Command(
            name = "market.show",
            aliases = {"ver"},
            async = true
    )
    public void showMarketCommand(Context<Player> context) {
        MarketInventory marketInventory = marketManager.getMarketInventory();
        marketInventory.openInventory(context.getSender());
    }

    @Command(
            name = "market.personal",
            aliases = {"pessoal"},
            async = true
    )
    public void personalMarketCommand(Context<Player> context) {

    }

    @Command(
            name = "market.sell",
            aliases = {"vender"},
            async = true
    )
    public void sellMarketCommand(Context<Player> context) {

    }

    @Command(
            name = "market.announced",
            aliases = {"anunciados"},
            async = true
    )
    public void announcedMarketCommand(Context<Player> context) {

    }

}
