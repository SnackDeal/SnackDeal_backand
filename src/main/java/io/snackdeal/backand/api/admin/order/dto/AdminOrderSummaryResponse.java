package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;

/** 관리자 주문 리스트 항목. 구매자 정보(email/name)를 함께 노출한다. */
public record AdminOrderSummaryResponse(
        Long orderId,
        String orderNumber,
        String buyerEmail,
        String buyerName,
        String mainProductName,
        Long finalAmount,
        OrderStatus status,
        LocalDateTime orderedAt
) {
}
