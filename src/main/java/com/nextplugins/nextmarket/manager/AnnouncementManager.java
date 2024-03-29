package com.nextplugins.nextmarket.manager;

import com.google.inject.Singleton;
import com.nextplugins.nextmarket.api.event.ProductCreateEvent;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.ConfigValue;
import com.nextplugins.nextmarket.util.NumberUtils;
import com.nextplugins.nextmarket.util.TextUtils;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Singleton
public final class AnnouncementManager {

    private final Map<String, Long> delayMap = new LinkedHashMap<>();

    public void sendCreationAnnounce(ProductCreateEvent event,
                                     String sellerMessage,
                                     String announcement,
                                     boolean hasDelay,
                                     Predicate<Player> filter) {

        Product product = event.getProduct();
        OfflinePlayer destination = product.getDestination();

        Player player = event.getPlayer();
        player.sendMessage(sellerMessage
                .replace("%price%", NumberUtils.formatNumber(product.getPrice()))
                .replace("%player%", destination != null && destination.getName() != null ? destination.getName() : "")
        );

        if (!ConfigValue.get(ConfigValue::useAnnouncementMessage)) return;

        val message = announcement
                .replace("%price%", NumberUtils.formatNumber(product.getPrice()))
                .replace("%player%", player.getName());

        sendTextComponent(player, message, hasDelay, () -> {

            TextComponent component = TextUtils.sendItemTooltipMessage(
                    message,
                    product.getItemStack());

            if (component == null) return null;

            String clickCommand = "/mercado " + (destination == null ?
                    "ver " + product.getCategory().getId() :
                    "pessoal"
            );

            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
            return component;
        }, filter);

    }

    private void sendTextComponent(Player player,
                                   String message,
                                   boolean hasDelay,
                                   Supplier<TextComponent> supplier,
                                   Predicate<Player> filter) {
        if (hasDelay) {
            if (inDelay(player)) return;
            insertDelay(player);
        }

        val textComponent = supplier.get();
        val component = textComponent == null ? new TextComponent(message) : textComponent;
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(filter)
                .forEach(target -> {
                    target.sendMessage("");
                    target.spigot().sendMessage(component);
                    target.sendMessage("");
                });

    }

    public void insertDelay(Player player) {
        delayMap.put(player.getName(), Instant.now().toEpochMilli());
    }

    public boolean inDelay(Player player) {
        Long delay = delayMap.get(player.getName());
        return delay != null && Instant.ofEpochMilli(delay)
                .plusSeconds(ConfigValue.get(ConfigValue::announcementSecondsDelay))
                .isAfter(Instant.now());
    }

}
