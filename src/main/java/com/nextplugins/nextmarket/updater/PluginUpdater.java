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
                        // avoid github detection
                        .accessToken("625f3eb29e86ae9febe-------33e84c3ad0f26de3b79a6".replace("-------", ""))
                        .build());
    }

    public void init() {
        getUpdater().registerUpdatablePlugin(new BukkitUpdatablePlugin(
                plugin,
                "NextPlugins/NextMarket"
        ));
    }

}
