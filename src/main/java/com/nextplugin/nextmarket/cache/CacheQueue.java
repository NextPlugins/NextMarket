package com.nextplugin.nextmarket.cache;

import com.nextplugin.nextmarket.util.TaskHelper;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

// Credits https://github.com/Luperzin/AquaFarming/blob/master/src/main/java/com/redescreen/farming/util/cache/CacheQueue.java
public class CacheQueue<T> {

    private final LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>();

    @Setter private Consumer<T> removalAction;
    private BukkitTask updateTask;

    @Setter private long updatePeriod;
    @Setter private TimeUnit timeUnit;

    public CacheQueue(long updatePeriod, TimeUnit timeUnit, boolean async) {
        this.updatePeriod = updatePeriod;
        this.timeUnit = timeUnit;

        updateTask = createTask(async);
    }

    public boolean stopQueue() {
        if (updateTask == null) return false;
        updateTask.cancel();
        updateTask = null;
        return true;
    }

    public void startQueue() {
        if (updateTask != null) return;
        updateTask = createTask(true);
    }

    public void addItem(T item) {
        if (!queue.contains(item))
            queue.add(item);
    }

    public void removeItem(Predicate<T> predicate) {
        queue.removeIf(predicate);
    }

    public void updateAll() {
        while (!queue.isEmpty()) {
            update();
        }
    }

    private void update() {
        T item = queue.poll();
        if (item == null) return;

        if (removalAction == null) return;
        removalAction.accept(item);
    }

    private BukkitTask createTask(boolean async) {
        return TaskHelper.scheduleRepeatingTask(
                this::updateAll,
                0,
                20 * timeUnit.toSeconds(updatePeriod),
                async
        );
    }

}
