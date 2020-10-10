package com.nextplugin.nextmarket.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugin.nextmarket.configuration.ConfigValue;
import com.nextplugin.nextmarket.inventory.ExpireItemsInventory;
import com.nextplugin.nextmarket.inventory.MarketInventory;
import com.nextplugin.nextmarket.inventory.PrivateMarketInventory;
import lombok.experimental.Accessors;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.entity.Player;

import java.util.List;

public class MarketCommand {

    private final ConfigValue configValue;

    @Inject
    public MarketCommand(ConfigValue configValue) {
        this.configValue = configValue;
    }

    @Command(
            name = "mercado",
            aliases = {"market"},
            permission = "nextmarket.use",
            description = "Comando principal do sistema de mercado.",
            target = CommandTarget.PLAYER
    )
    public void marketCommand(Context<Player> context) {

        Player player = context.getSender();

        List<String> messages = configValue.commandMessage();

        for (String message : messages) {
            player.sendMessage(message);
        }

    }

    @Command(name = "mercado.ver")
    public void viewMarket(Context<Player> context) {

        Player player = context.getSender();

        MarketInventory marketInventory = new MarketInventory();

        marketInventory.openInventory(player);

    }

    @Command(name = "mercado.pessoal")
    public void viewPersonalMarket(Context<Player> context) {

        Player player = context.getSender();

        PrivateMarketInventory privateMarketInventory = new PrivateMarketInventory();

        privateMarketInventory.openInventory(player);

    }

    @Command(name = "mercado.vender")
    public void announceItemOnMarket(Context<Player> context, double value, @Optional Player target) {

        Player player = context.getSender();

        double maxValue = configValue.maximumAnnouncementValue();
        double minValue = configValue.minimumAnnouncementValue();

        if (value > maxValue) {
            player.sendMessage(configValue.maximumValueReachedMessage());
        }
        if (value < minValue) {
            player.sendMessage(configValue.minimumValueNotReachedMessage());
        }

        if (target != null) {

            // TODO sells on target's private market

        }

        // TODO sells on global market

    }

    @Command(name = "mercado.expirados")
    public void viewMarketExpiredItems(Context<Player> context) {

        Player player = context.getSender();

        ExpireItemsInventory expireItemsInventory = new ExpireItemsInventory();

        expireItemsInventory.openInventory(player);

    }

    @Command(name = "mercado.anunciados")
    public void viewMarketAnnouncedItems(Context<Player> context) {

        // TODO announced items inventory

    }

}
