package com.nextplugins.nextmarket.api;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.nextplugins.nextmarket.api.model.category.Category;
import com.nextplugins.nextmarket.api.model.product.Product;
import com.nextplugins.nextmarket.manager.CategoryManager;
import com.nextplugins.nextmarket.storage.ProductStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NextMarketAPI {

    @Getter public static final NextMarketAPI instance = new NextMarketAPI();

    @Inject private CategoryManager categoryManager;
    @Inject private ProductStorage productStorage;

    /**
     * Search all categories to look for one with the entered id.
     *
     * @param id category id to search
     * @return {@link Optional} with the category found
     */
    public Optional<Category> findCategoryById(String id) {
        return categoryManager.findCategoryById(id);
    }

    /**
     * Search all categories to look for one with the entered custom filter.
     *
     * @param filter custom filter to search
     * @return {@link Optional} with the category found
     */
    public Optional<Category> findCategoryByFilter(Predicate<Category> filter) {
        return allCategories().stream()
                .filter(filter)
                .findFirst();
    }

    /**
     * Search all categories to look for every with the entered custom filter.
     *
     * @param filter custom filter to search
     * @return {@link Set} with all categories found
     */
    public Set<Category> findCategoriesByFilter(Predicate<Category> filter) {
        return allCategories().stream()
                .filter(filter)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieve all products from the category.
     *
     * @param category category to search products
     * @return {@link Set} with all products found
     */
    public Set<Product> findProductsByCategory(Category category) {
        return productStorage.findProductsByCategory(category);
    }

    /**
     * Retrieve all products from the seller.
     *
     * @param uniqueId products seller {@link UUID}
     * @return {@link Set} with all products found
     */
    public Set<Product> findProductsBySeller(UUID uniqueId) {
        return productStorage.findProductsBySeller(uniqueId);
    }

    /**
     * Retrieve all products to destination player.
     *
     * @param uniqueId products destination {@link UUID}
     * @return {@link Set} with all products found
     */
    public Set<Product> findProductsByDestination(UUID uniqueId) {
        return productStorage.findProductsByDestination(uniqueId);
    }

    /**
     * Search all products to look for one with the entered custom filter.
     *
     * @param filter custom filter to search
     * @return {@link Optional} with the product found
     */
    public Optional<Product> findProductByFilter(Predicate<Product> filter) {
        return allProducts().stream()
                .filter(filter)
                .findFirst();
    }

    /**
     * Search all products to look for every with the entered custom filter.
     *
     * @param filter custom filter to search
     * @return {@link Set} with all products found
     */
    public Set<Product> findProductsByFilter(Predicate<Product> filter) {
        return allProducts().stream()
                .filter(filter)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieve all categories registered in configuration.
     *
     * @return {@link Set} with categories
     */
    public Set<Category> allCategories() {
        return ImmutableSet.copyOf(categoryManager.getCategoryMap().values());
    }

    /**
     * Retrieve all products loaded so far.
     *
     * @return {@link Set} with products
     */
    public Set<Product> allProducts() {
        Set<Product> products = new LinkedHashSet<>();
        productStorage.getProducts().values().forEach(products::addAll);
        return ImmutableSet.copyOf(products);
    }

}
