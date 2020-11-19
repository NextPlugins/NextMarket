package com.nextplugin.nextmarket.hook;

import com.nextplugin.nextmarket.NextMarket;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    public Economy getEconomy() {
        RegisteredServiceProvider<Economy> rsp = NextMarket.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        return rsp.getProvider();
    }

}
