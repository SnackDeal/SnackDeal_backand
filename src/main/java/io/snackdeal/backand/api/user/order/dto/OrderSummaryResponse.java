package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;

/** 주문 목록 항목 응답. mainProductName = 대표 상품명, itemCount = 주문 상품 종류 수. */
public record OrderSummaryResponse(
        Long orderId,
        String orderNumber,
        LocalDateTime orderedAt,
        String mainProductName,
        int itemCount,
        Long finalAmount,
        OrderStatus status
) {
}
