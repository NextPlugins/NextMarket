package com.nextplugin.nextmarket.command;

import com.nextplugin.nextmarket.configuration.ConfigValue;
import com.nextplugin.nextmarket.inventory.ExpireItemsInventory;
import com.nextplugin.nextmarket.inventory.MarketInventory;
import com.nextplugin.nextmarket.inventory.PrivateMarketInventory;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MarketCommand {

    private final ConfigValue configValue;

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
    public void marketCommand(Context<Player> context){

        Player player = context.getSender();

        configValue.commandMessage().forEach(player::sendMessage);

    }

    @Command(name = "mercado.ver")
    public void viewMarket(Context<Player> context){

        Player player = context.getSender();

        MarketInventory marketInventory = new MarketInventory();

        marketInventory.openInventory(player);

    }

    @Command(name = "mercado.pessoal")
    public void viewPersonalMarket(Context<Player> context){

        Player player = context.getSender();

        PrivateMarketInventory privateMarketInventory = new PrivateMarketInventory();

        privateMarketInventory.openInventory(player);

    }

    @Command(name = "mercado.vender")
    public void announceItemOnMarket(Context<Player> context, double value, @Optional Player target){

        Player player = context.getSender();

        if(!target.isOnline()){
            player.sendMessage(configValue.offlinePlayerMessage());
        }

    }

    @Command(name = "mercado.expirados")
    public void viewMarketExpiredItems(Context<Player> context){

        Player player = context.getSender();

        ExpireItemsInventory expireItemsInventory = new ExpireItemsInventory();

        expireItemsInventory.openInventory(player);

    }

    @Command(name = "mercado.anunciados")
    public void viewMarketAnnouncedItems(Context<Player> context){



    }

}