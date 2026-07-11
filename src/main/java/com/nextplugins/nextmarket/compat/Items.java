package com.nextplugins.nextmarket.compat;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.nextplugins.nextmarket.NextMarket;
import com.nextplugins.nextmarket.api.model.product.MaterialData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Cross-version item creation and decoration using XSeries.
 */
public final class Items {

    private Items() {
    }

    public static Optional<XMaterial> matchMaterial(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return XMaterial.matchXMaterial(name.trim());
    }

    /**
     * Parses {@code MATERIAL} or {@code MATERIAL:data} (and {@code MATERIAL:all} for ignore-data).
     */
    public static MaterialData parseMaterialData(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        String materialName = raw.trim();
        Integer data = null;
        boolean ignoreData = true;

        if (materialName.contains(":")) {
            String[] args = materialName.split(":", 2);
            materialName = args[0].trim();
            String type = args[1].trim();
            if (!type.equalsIgnoreCase("all")) {
                try {
                    data = Integer.parseInt(type);
                    ignoreData = false;
                } catch (NumberFormatException e) {
                    warn("Data inválida em material '" + raw + "'.");
                    return null;
                }
            }
        }

        Optional<XMaterial> matched = matchMaterial(materialName);
        if (!matched.isPresent()) {
            // Retry with data embedded for XMaterial (WOOL:14 style already handled by XMaterial)
            if (data != null) {
                matched = matchMaterial(materialName + ":" + data);
            }
        }

        if (!matched.isPresent()) {
            warn("Material desconhecido (ignorado nesta versão): " + raw);
            return null;
        }

        XMaterial xMaterial = matched.get();
        if (!xMaterial.isSupported()) {
            warn("Material não suportado nesta versão do Minecraft: " + raw);
            return null;
        }

        return new MaterialData(xMaterial, ignoreData ? null : data, ignoreData);
    }

    public static ItemStack create(String materialName, int data) {
        MaterialData materialData = parseMaterialData(data > 0 ? materialName + ":" + data : materialName);
        if (materialData == null) {
            // Try plain name + separate data for legacy configs
            Optional<XMaterial> matched = matchMaterial(materialName);
            if (!matched.isPresent() || !matched.get().isSupported()) {
                warn("Não foi possível criar o item: " + materialName + (data > 0 ? ":" + data : ""));
                return fallbackBarrier();
            }
            materialData = new MaterialData(matched.get(), data > 0 ? data : null, data <= 0);
        }
        ItemStack stack = materialData.toItemStack(1);
        return stack != null ? stack : fallbackBarrier();
    }

    public static ItemStack create(String materialName) {
        return create(materialName, 0);
    }

    public static ItemStack named(ItemStack itemStack, String displayName, List<String> lore) {
        if (itemStack == null) return null;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
        if (lore != null) {
            meta.setLore(lore);
        }
        applySafeFlags(meta);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack named(ItemStack itemStack, String displayName, String... lore) {
        return named(itemStack, displayName, lore == null ? Collections.<String>emptyList() : Arrays.asList(lore));
    }

    public static void applyGlow(ItemStack itemStack) {
        if (itemStack == null) return;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        Enchantment enchantment = resolveGlowEnchantment();
        if (enchantment != null) {
            meta.addEnchant(enchantment, 1, true);
        }
        applySafeFlags(meta);
        itemStack.setItemMeta(meta);
    }

    public static void applySafeFlags(ItemMeta meta) {
        if (meta == null) return;
        // Avoid ItemFlag.values() — new flags on modern versions can break older items/APIs.
        try {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        } catch (Throwable ignored) {
            try {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            } catch (Throwable ignored2) {
            }
        }
    }

    public static Enchantment resolveGlowEnchantment() {
        try {
            Enchantment unbreaking = XEnchantment.UNBREAKING.get();
            if (unbreaking != null) return unbreaking;
        } catch (Throwable ignored) {
        }

        try {
            Optional<XEnchantment> durability = XEnchantment.of("DURABILITY");
            if (durability.isPresent()) {
                Enchantment enchantment = durability.get().get();
                if (enchantment != null) return enchantment;
            }
        } catch (Throwable ignored) {
        }

        Enchantment[] values = Enchantment.values();
        return values.length > 0 ? values[0] : null;
    }

    public static ItemStack fallbackBarrier() {
        Optional<XMaterial> barrier = matchMaterial("BARRIER");
        if (barrier.isPresent() && barrier.get().isSupported()) {
            ItemStack stack = barrier.get().parseItem();
            if (stack != null) return stack;
        }
        Optional<XMaterial> bedrock = matchMaterial("BEDROCK");
        if (bedrock.isPresent() && bedrock.get().isSupported()) {
            ItemStack stack = bedrock.get().parseItem();
            if (stack != null) return stack;
        }
        return new ItemStack(Material.STONE);
    }

    private static void warn(String message) {
        try {
            Logger logger = NextMarket.getInstance().getLogger();
            logger.warning(message);
        } catch (Throwable ignored) {
            // Plugin may not be fully enabled yet
        }
    }

}
