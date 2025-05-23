package turtleMart.product.dto;

import turtleMart.product.entity.Product;

public record ProductResponseForSeller(
        Long productId,
        String name,
        int price,
        String description
) {
    public static ProductResponseForSeller from(Product product) {
        return new ProductResponseForSeller(product.getId(), product.getName(), product.getPrice(), product.getDescription());
    }
}
