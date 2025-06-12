package turtleMart.product.dto;


import turtleMart.global.kafka.enums.OperationType;

public record ProductOptionCombinationInventoryDto(
        Long productOptionCombinationId,
        Integer inventory,
        String operationId,
        OperationType operationType
) {
    public static ProductOptionCombinationInventoryDto of(Long productOptionCombinationId, Integer inventory,String operationId,OperationType operationType) {
        return new ProductOptionCombinationInventoryDto(productOptionCombinationId, inventory,operationId,operationType);
    }
}
