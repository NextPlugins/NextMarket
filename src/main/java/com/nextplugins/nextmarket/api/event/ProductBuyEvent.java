package com.nextplugins.nextmarket.api.event;

import com.nextplugins.nextmarket.api.model.product.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@EqualsAndHashCode(callSuper = true)
@Data
public final class ProductBuyEvent extends CustomEvent implements Cancellable {

    /*
     * Este evento é chamado quando um item é comprado do mercado.
     */

    private final Player player;
    private final Product product;
    private boolean cancelled;

}
