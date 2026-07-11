package com.nextplugins.nextmarket.command;

import com.google.inject.Inject;
import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.event.ProductCreateEvent;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.configuration.value.MessageValue;
import com.nextplugins.nextmarket.inventory.PersonalMarketInventory;
import com.nextplugins.nextmarket.inventory.SellingMarketInventory;
import com.nextplugins.nextmarket.manager.CategoryManager;
import com.nextplugins.nextmarket.manager.ProductManager;
import com.nextplugins.nextmarket.registry.InventoryRegistry;
import com.nextplugins.nextmarket.storage.ProductStorage;
import com.nextplugins.nextmarket.util.MessageUtils;
import com.nextplugins.nextmarket.util.NumberUtils;
import lombok.val;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MarketCommand {

    @Inject private CategoryManager categoryManager;

    @Inject private ProductManager productManager;
    @Inject private ProductStorage productStorage;

    @Inject private InventoryRegistry inventoryRegistry;

    @Command(
            name = "mercado",
            aliases = {"market"},
            permission = "nextmarket.use",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void marketCommand(Context<Player> context) {
        context.sendMessage(MessageValue.get(MessageValue::commandMessage).toArray(new String[]{}));
    }

    @Command(
            name = "mercado.info",
            permission = "nextmarket.admin",
            target = CommandTarget.ALL,
            async = true
    )
    public void infoCommand(Context<CommandSender> context) {
        val updateChecker = NextMarket.getInstance().getUpdateChecker();

        double itemCount = categoryManager.getCategoryMap().values()
                .stream()
                .map(Category::getConfiguration)
                .map(config -> config.getMaterials().size() + config.getNbts().size() + config.getNames().size())
                .mapToInt(Integer::intValue)
                .sum();

        context.sendMessage("");
        context.sendMessage(MessageUtils.colored(" &6&lNextMarket &f- &eInformações"));
        context.sendMessage("");
        context.sendMessage(MessageUtils.colored("  &fCategorias: &e" + categoryManager.getCategoryMap().size()));
        context.sendMessage(MessageUtils.colored("  &fItens registrados: &e" + itemCount));
        context.sendMessage(MessageUtils.colored("  &fVersão atual: &e" + updateChecker.getCurrentVersion()));
        context.sendMessage(MessageUtils.colored("  &fServidor: &e" + com.nextplugins.nextmarket.compat.ServerVersion.asString()));

        if (!MessageUtils.sendUpdateMessage(context.getSender())) {
            context.sendMessage("");
        }
    }

    @Command(
            name = "mercado.ver",
            aliases = {"show"},
            async = true
    )
    public void showMarketCommand(Context<Player> context, @Optional String id) {
        Player player = context.getSender();
        if (id == null) {
            runSync(() -> inventoryRegistry.getMarketInventory().openInventory(player));
            return;
        }

        Category category = categoryManager.findCategoryById(id).orElse(null);
        if (category == null) {
            context.sendMessage(MessageValue.get(MessageValue::categoryNotExists));
            return;
        }

        runSync(() -> inventoryRegistry.getCategoryInventory().openInventory(
                player,
                category,
                productStorage.findProductsByCategory(category)
        ));
    }

    @Command(
            name = "mercado.pessoal",
            aliases = {"personal"},
            async = true
    )
    public void personalMarketCommand(Context<Player> context) {
        Player player = context.getSender();
        runSync(() -> {
            try {
                inventoryRegistry.getPersonalMarketInventory().openInventory(player);
            } catch (Throwable ignored) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
            }
        });
    }

    @Command(
            name = "mercado.vender",
            aliases = {"sell"},
            usage = "/mercado vender <valor> [jogador]",
            async = true
    )
    public void sellMarketCommand(Context<Player> context, String priceText, @Optional String destination) {
        if (priceText == null) {
            context.sendMessage(MessageValue.get(MessageValue::correctUsageSellMessage));
            return;
        }

        double price = NumberUtils.parse(priceText);
        if (NumberUtils.isInvalid(price)) {
            context.getSender().sendMessage(MessageValue.get(MessageValue::invalidNumber));
            return;
        }

        Player player = context.getSender();
        Product product = productManager.createProduct(player, destination, price);
        if (product == null) return;

        runSync(() -> inventoryRegistry.getConfirmationInventory().openConfirmation(
                player,
                "Venda de item",
                () -> {
                    ProductCreateEvent createEvent = new ProductCreateEvent(player, product);
                    Bukkit.getPluginManager().callEvent(createEvent);
                },
                product.getItemStack()
        ));
    }

    @Command(
            name = "mercado.anunciados",
            aliases = {"selling", "vendidos"},
            async = true
    )
    public void sellingMarketCommand(Context<Player> context) {
        Player player = context.getSender();
        runSync(() -> {
            try {
                inventoryRegistry.getSellingMarketInventory().openInventory(player);
            } catch (Throwable ignored) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Não existe itens nesta categoria.");
            }
        });
    }

    private void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return;
        }
        Bukkit.getScheduler().runTask(NextMarket.getInstance(), runnable);
    }

}

