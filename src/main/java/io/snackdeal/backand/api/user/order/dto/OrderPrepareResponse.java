package io.snackdeal.backand.api.user.order.dto;

/** 주문 준비 응답(결제 전 금액 확정). */
public record OrderPrepareResponse(
        String orderNumber,
        Long productAmount,
        Long shippingFee,
        Long discountAmount,
        Long finalAmount
) {
}
