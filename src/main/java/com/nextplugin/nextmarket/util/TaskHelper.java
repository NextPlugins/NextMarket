package com.nextplugin.nextmarket.util;

import com.nextplugin.nextmarket.NextMarket;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Callable;

public class TaskHelper {

    public static void callAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(NextMarket.getInstance(), runnable);
    }

    public static <T> void callSync(Callable<T> callable) {
        Bukkit.getScheduler().callSyncMethod(NextMarket.getInstance(), callable);
    }

    public static BukkitTask scheduleRepeatingTask(Runnable runnable, long delay,
                                                   long period, boolean async) {
        if (!async) {
            return Bukkit.getScheduler().runTaskTimer(NextMarket.getInstance(), runnable, delay, period);
        } else {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(NextMarket.getInstance(), runnable, delay, period);
        }
    }

    public static BukkitTask scheduleDelayingTask(Runnable runnable, long delay, boolean async) {
        if (!async) {
            return Bukkit.getScheduler().runTaskLater(NextMarket.getInstance(), runnable, delay);
        } else {
            return Bukkit.getScheduler().runTaskLaterAsynchronously(NextMarket.getInstance(), runnable, delay);
        }
    }
}
