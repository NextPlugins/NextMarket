package com.nextplugins.nextmarket.api.event;

import com.nextplugins.nextmarket.api.model.product.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@EqualsAndHashCode(callSuper = true)
@Data
public final class ProductRemoveEvent extends CustomEvent implements Cancellable {

    /*
     * Este evento é chamado quando um item é removido do mercado manualmente.
     */

    private final Player player;
    private final Product product;
    private boolean cancelled;

}
