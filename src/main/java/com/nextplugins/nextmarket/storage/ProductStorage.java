package com.nextplugins.nextmarket.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.dao.repository.ProductRepository;
import com.nextplugins.nextmarket.manager.ProductManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public final class ProductStorage {

    private final Map<Category, Set<Product>> products = new LinkedHashMap<>();

    @Inject private ProductManager productManager;
    @Inject private ProductRepository productRepository;

    public void init() {
        productRepository.createTable();
        for (Product product : productRepository.selectAll()) {
            productManager.insertProductCategory(product);
            addProduct(product);
        }
    }

    public void insertOne(Product product) {
        this.addProduct(product);
        productRepository.insertOne(product);
    }

    public void deleteOne(Product product) {
        this.removeProduct(product);
        productRepository.deleteOne(product);
    }

    public Set<Product> findProductsByCategory(Category category) {
        return ImmutableSet.copyOf(products.getOrDefault(category, Collections.emptySet()).stream()
                .filter(product -> product.getDestination() == null)
                .filter(product -> !product.isExpired())
                .collect(Collectors.toList())
        );
    }

    public Set<Product> findProductsBySeller(UUID uniqueId) {
        Set<Product> playerProducts = new LinkedHashSet<>();
        for (Set<Product> products : products.values()) {
            for (Product product : products) {
                if (product.getSeller().getUniqueId().equals(uniqueId)) {
                    playerProducts.add(product);
                }
            }
        }
        return ImmutableSet.copyOf(playerProducts);
    }

    public Set<Product> findProductsBySeller(Player player) {
        return findProductsBySeller(player.getUniqueId());
    }

    public Set<Product> findProductsByDestination(UUID uniqueId) {
        Set<Product> playerProducts = new LinkedHashSet<>();
        for (Set<Product> products : products.values()) {
            for (Product product : products) {
                OfflinePlayer destination = product.getDestination();
                if (destination == null) continue;

                if (destination.getUniqueId().equals(uniqueId)) {
                    playerProducts.add(product);
                }
            }
        }
        return ImmutableSet.copyOf(playerProducts);
    }

    public Set<Product> findProductsByDestination(Player player) {
        return findProductsByDestination(player.getUniqueId());
    }

    public Map<Category, Set<Product>> getProducts() {
        return ImmutableMap.copyOf(this.products);
    }

    private void addProduct(Product product) {
        Set<Product> products = this.products.computeIfAbsent(product.getCategory(), k -> new LinkedHashSet<>());
        products.add(product);
    }

    private void removeProduct(Product product) {
        Set<Product> products = this.products.computeIfAbsent(product.getCategory(), k -> new LinkedHashSet<>());
        products.remove(product);
    }

}
