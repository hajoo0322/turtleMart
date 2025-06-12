package turtleMart.product.dto;


import turtleMart.global.kafka.enums.OperationType;

public record ProductOptionCombinationPriceDto(
        Long productOptionCombinationId,
        Integer price,
        String operationId,
        OperationType operationType
) {
    public static ProductOptionCombinationPriceDto of(Long productOptionCombinationId, Integer price, String operationId, OperationType operationType) {
        return new ProductOptionCombinationPriceDto(
                productOptionCombinationId,
                price,
                operationId,
                operationType
        );
    }
}
