package io.snackdeal.backand.domain.order.entity;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_COMPLETED,
    PREPARING_SHIPMENT,
    SHIPPED,
    COMPLETED,
    CANCELLED,
    REFUND_REQUESTED,
    REFUND_COMPLETED
}
