package com.nextplugins.nextmarket.api.model.product;

import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.configuration.value.ConfigValue;
import com.nextplugins.nextmarket.util.NumberUtils;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ToString(exclude = {"category"})
@EqualsAndHashCode(of = "uniqueId")
@Builder
@Data
public final class Product {

    @Builder.Default private final UUID uniqueId = UUID.randomUUID();

    private final OfflinePlayer seller;
    private final OfflinePlayer destination;

    private final ItemStack itemStack;
    private final double price;

    @Builder.Default private final Instant createAt = Instant.now();

    private transient Category category;
    @Builder.Default private transient boolean available = true;

    public void setCategory(Category category) {
        if (this.category != null) throw new UnsupportedOperationException("category has already defined");
        this.category = category;
    }

    public boolean isExpired() {
        return createAt.plusSeconds(ConfigValue.get(ConfigValue::announcementExpireTime)).isBefore(Instant.now());
    }

    /**
     * Returns item stack with details to market
     *
     * @return item stack
     */
    public ItemStack toViewItemStack(List<String> lore) {
        ItemStack itemStack = this.itemStack.clone();

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> itemLore = itemMeta.getLore();
        if (itemLore == null) itemLore = new LinkedList<>();
        itemLore.addAll(lore
                .stream()
                .map(line -> line
                        .replace("%seller%", this.seller.getName())
                        .replace("%price%", NumberUtils.formatNumber(this.price)))
                .collect(Collectors.toList())
        );
        itemMeta.setLore(itemLore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
