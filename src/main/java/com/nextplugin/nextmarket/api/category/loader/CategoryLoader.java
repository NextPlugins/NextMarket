package com.nextplugin.nextmarket.api.category.loader;

import com.nextplugin.nextmarket.NextMarket;
import com.nextplugin.nextmarket.api.category.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CategoryLoader {

    private final NextMarket plugin;

    public CategoryLoader(NextMarket plugin) {
        this.plugin = plugin;
    }

    public NextMarket getPlugin() {
        return plugin;
    }

    public List<Category> load() {
        final FileConfiguration config = plugin.getCategoriesConfiguration();

        List<Category> categories = new ArrayList<>();
        List<String> values = config.getStringList("categories-item");

        for (String key : config.getConfigurationSection("categories").getKeys(false)) {

            final String path = "categories." + key;

            String display = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".item.display"));

            List<String> lore = config.getStringList(path + ".item.lore")
                    .stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());


            short data = (short) config.getInt(path + ".item.data");

            Material material = Material.getMaterial(config.getString(path + "."));

            List<ItemFlag> flags = config.getStringList(path + ".item.flags")
                    .stream()
                    .map(ItemFlag::valueOf)
                    .collect(Collectors.toList());

            final ItemStack icon = new ItemStack(material, 1, data);
            ItemMeta meta = icon.getItemMeta();

            meta.setDisplayName(display);
            meta.setLore(lore);
            meta.addItemFlags((ItemFlag[]) flags.toArray());

            icon.setItemMeta(meta);

            List<Material> validTypes = new ArrayList<>();

            for (String type : values) {
                String[] dataType = type.split(":");

                if (dataType[1].equalsIgnoreCase(key)) {
                    validTypes.add(Material.getMaterial(dataType[0]));
                }
            }

            final int position = config.getInt(path + ".item.slot") - 1; // inventory slots starts at 0

            Category category = new Category(icon, validTypes, new ArrayList<>(), position);

            categories.add(category);
        }

        return categories;
    }

}
