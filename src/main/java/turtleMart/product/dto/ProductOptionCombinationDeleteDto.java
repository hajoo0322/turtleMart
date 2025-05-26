package turtleMart.product.dto;

import turtleMart.global.common.OperationType;

public record ProductOptionCombinationDeleteDto(
        Long productOptionCombinationId,
        String operationId,
        OperationType operationType

) {
    public static ProductOptionCombinationDeleteDto of(Long productOptionCombinationId, String operationId, OperationType operationType) {
        return new ProductOptionCombinationDeleteDto(productOptionCombinationId, operationId, operationType);
    }
}
