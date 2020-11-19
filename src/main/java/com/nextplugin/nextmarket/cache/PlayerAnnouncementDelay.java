package com.nextplugin.nextmarket.cache;

import lombok.Data;
import org.bukkit.entity.Player;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@Singleton
public class PlayerAnnouncementDelay {

    private Map<String, Long> delayMap = new HashMap<>();

    /**
     *
     * @param player check if <code>Player</code> is in delay
     * @return whether the player is in delay or not
     */
    public boolean inDelay(Player player) {
        return delayMap.containsKey(player.getName()) && delayMap.get(player.getName()) > System.currentTimeMillis();
    }

    /**
     * Delay the player for <code>time</code> seconds
     * @param player the <code>Player</code>
     * @param time the <code>time</code> in secconds
     */
    public void putDelay(Player player, int time) {
        delayMap.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(time));
    }
}
