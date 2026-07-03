package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

/** 주문 상세 응답. */
public record OrderResponse(
        Long id,
        String orderNumber,
        Long productAmount,
        Long shippingFee,
        Long discountAmount,
        Long finalAmount,
        OrderStatus status,
        LocalDateTime orderedAt,
        List<OrderItemResponse> items
) {
}
