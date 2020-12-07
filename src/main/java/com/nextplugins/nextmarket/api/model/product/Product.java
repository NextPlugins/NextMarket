package com.nextplugins.nextmarket.api.model.product;

import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.configuration.value.ConfigValue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Henry Fábio
 */
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

}
