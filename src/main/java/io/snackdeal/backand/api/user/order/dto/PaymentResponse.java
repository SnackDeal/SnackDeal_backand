package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.PaymentStatus;

import java.time.LocalDateTime;

/** 주문 상세의 결제 정보 응답 (금액 내역 + 결제수단). */
public record PaymentResponse(
        Long productAmount,
        Long shippingFee,
        String couponName,
        Long discountAmount,
        Long finalAmount,
        String payMethod,
        String pgProvider,
        PaymentStatus status,
        String receiptUrl,
        LocalDateTime paidAt
) {
}
