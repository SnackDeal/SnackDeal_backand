package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.PaymentStatus;

import java.time.LocalDateTime;

/** 결제 검증 성공 → 주문 확정 응답. */
public record OrderCompleteResponse(
        Long orderId,
        String orderNumber,
        OrderStatus status,
        Long productAmount,
        Long shippingFee,
        Long discountAmount,
        Long finalAmount,
        Payment payment,
        LocalDateTime paidAt
) {
    /** 확정된 결제 요약. */
    public record Payment(
            String impUid,
            String payMethod,
            String pgProvider,
            PaymentStatus status,
            String receiptUrl
    ) {
    }
}
