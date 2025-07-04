package turtleMart.global.kafka.enums;

public enum OperationType {
    PRICE_CHANGE,
    INVENTORY_OVERRIDE,
    COMBINATION_DELETE,
    INVENTORY_UPDATE,
    COMBINATION_STATUS,
    ORDER_CREATE,
    ORDER_PAYMENT_INVENTORY_DECREASE,
    DELIVERY_FAIL_INVENTORY_RESTORE,
    DELIVERY_CREATE
}
