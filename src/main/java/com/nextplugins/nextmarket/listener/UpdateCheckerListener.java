package com.nextplugins.nextmarket.listener;

import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class UpdateCheckerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("nextmarket.admin")) return;
        Bukkit.getScheduler().runTaskLater(NextMarket.getInstance(), () -> MessageUtils.sendUpdateMessage(event.getPlayer()), 60L);
    }


}
