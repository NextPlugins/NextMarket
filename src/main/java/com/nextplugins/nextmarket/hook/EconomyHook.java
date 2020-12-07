package com.nextplugins.nextmarket.hook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Logger;

/**
 * @author Henry Fábio
 */
@Singleton
public final class EconomyHook {

    @Inject @Named("main") private Logger logger;
    private Economy economy;

    public void init() {
        RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registration == null) {
            logger.severe("Não foi encontrado nenhum plugin de economia no servidor!");
        } else {
            economy = registration.getProvider();
        }
    }

    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public EconomyResponse depositCoins(OfflinePlayer player, double amount) {
        return economy.depositPlayer(player, amount);
    }

    public EconomyResponse withdrawCoins(OfflinePlayer player, double amount) {
        return economy.withdrawPlayer(player, amount);
    }

}
