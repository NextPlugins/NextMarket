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

    // player name # time
    private Map<String, Long> delayMap = new HashMap<>();

    public boolean inDelay(Player player) {
        return delayMap.containsKey(player.getName()) && delayMap.get(player.getName()) > System.currentTimeMillis();
    }

    public void putDelay(Player player, int time) {
        delayMap.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(time));
    }
}
