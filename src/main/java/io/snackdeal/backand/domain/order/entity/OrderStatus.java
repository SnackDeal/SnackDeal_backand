package io.snackdeal.backand.domain.order.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상태: PENDING_PAYMENT(결제대기) / PAYMENT_COMPLETED(결제완료) / PREPARING_SHIPMENT(배송준비중) / SHIPPED(배송중) / COMPLETED(구매확정) / CANCELLED(취소) / REFUND_REQUESTED(환불요청) / REFUND_COMPLETED(환불완료)")
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
