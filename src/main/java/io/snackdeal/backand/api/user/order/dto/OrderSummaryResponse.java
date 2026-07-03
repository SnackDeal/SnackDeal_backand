package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;

/** 주문 목록 항목 응답. */
public record OrderSummaryResponse(
        Long id,
        String orderNumber,
        Long finalAmount,
        OrderStatus status,
        LocalDateTime orderedAt
) {
}
