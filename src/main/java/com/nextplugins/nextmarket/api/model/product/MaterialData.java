package com.nextplugins.nextmarket.api.model.product;

import com.cryptomorin.xseries.XMaterial;
import com.nextplugins.nextmarket.compat.Items;
import com.nextplugins.nextmarket.compat.ServerVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Cross-version material reference. Prefer {@link XMaterial} over raw Bukkit {@link Material}
 * so the same config works from 1.8 through modern releases.
 */
@Getter
@AllArgsConstructor
public class MaterialData {

    private final XMaterial xMaterial;
    /** Legacy durability/data; only meaningful on pre-1.13 when {@link #ignoreData} is false. */
    private final Integer data;
    private final boolean ignoreData;

    public static MaterialData of(@NonNull ItemStack item, boolean ignoreData) {
        XMaterial matched = XMaterial.matchXMaterial(item);
        Integer data = null;
        if (!ignoreData && ServerVersion.isLegacy()) {
            data = (int) item.getDurability();
        }
        return new MaterialData(matched, data, ignoreData);
    }

    /**
     * Legacy constructor kept for call sites that still pass Bukkit Material.
     * Prefer constructors that take {@link XMaterial}.
     */
    public MaterialData(Material material, int data, boolean ignoreData) {
        XMaterial matched = material == null ? XMaterial.STONE : XMaterial.matchXMaterial(material);
        this.xMaterial = matched;
        this.data = ignoreData ? null : data;
        this.ignoreData = ignoreData;
    }

    public Material getMaterial() {
        Material parsed = xMaterial != null ? xMaterial.parseMaterial() : null;
        return parsed != null ? parsed : Material.STONE;
    }

    public int getData() {
        if (data != null) return data;
        if (xMaterial != null) return xMaterial.getData();
        return 0;
    }

    public ItemStack toItemStack(int quantity) {
        if (xMaterial == null || !xMaterial.isSupported()) {
            return Items.fallbackBarrier();
        }

        ItemStack stack = xMaterial.parseItem();
        if (stack == null) {
            return Items.fallbackBarrier();
        }

        stack.setAmount(Math.max(1, quantity));

        if (!ignoreData && data != null && ServerVersion.isLegacy()) {
            stack.setDurability(data.shortValue());
        }

        return stack;
    }

    public boolean matches(ItemStack itemStack) {
        if (itemStack == null || xMaterial == null) return false;
        XMaterial other = XMaterial.matchXMaterial(itemStack);
        if (other != xMaterial) return false;
        if (ignoreData || !ServerVersion.isLegacy() || data == null) return true;
        return itemStack.getDurability() == data.shortValue();
    }

    public boolean equals(MaterialData materialData) {
        if (materialData == null || xMaterial == null || materialData.xMaterial == null) return false;
        if (xMaterial != materialData.xMaterial) return false;
        if (ignoreData || materialData.ignoreData || !ServerVersion.isLegacy()) return true;
        int thisData = data != null ? data : 0;
        int otherData = materialData.data != null ? materialData.data : 0;
        return thisData == otherData;
    }

}
