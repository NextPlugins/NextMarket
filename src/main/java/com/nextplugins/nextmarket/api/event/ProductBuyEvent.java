package com.nextplugins.nextmarket.api.event;

import com.nextplugins.nextmarket.api.model.product.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Evento chamado quando um item é comprado do mercado.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public final class ProductBuyEvent extends CustomEvent implements Cancellable {

    private final Player player;
    private final Product product;
    private boolean cancelled;

}
