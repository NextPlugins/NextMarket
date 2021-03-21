package com.nextplugins.nextmarket.updater;

import com.henryfabio.minecraft.githubupdater.api.configuration.UpdaterConfiguration;
import com.henryfabio.minecraft.githubupdater.api.credentials.GithubCredentials;
import com.henryfabio.minecraft.githubupdater.bukkit.BukkitGithubUpdater;
import com.henryfabio.minecraft.githubupdater.bukkit.plugin.BukkitUpdatablePlugin;
import com.nextplugins.nextmarket.NextMarket;
import lombok.Getter;

public final class PluginUpdater {

    private static final NextMarket plugin = NextMarket.getInstance();

    @Getter private final BukkitGithubUpdater updater;

    public PluginUpdater() {
        this.updater = new BukkitGithubUpdater(
                plugin,
                UpdaterConfiguration.DEFAULT,
                GithubCredentials.builder()
                        .username("eikefab")
                        .accessToken("74d445235d40c503aaa51746eba383f6a51573fa")
                        .build());
    }

    public void init() {
        getUpdater().registerUpdatablePlugin(new BukkitUpdatablePlugin(
                plugin,
                "NextPlugins/NextMarket"
        ));
    }

}
