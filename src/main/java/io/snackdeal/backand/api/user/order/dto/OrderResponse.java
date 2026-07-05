package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

/** 주문 상세 응답 (상품별 내역 + 배송지 + 결제 정보). */
public record OrderResponse(
        Long orderId,
        String orderNumber,
        LocalDateTime orderedAt,
        OrderStatus status,
        List<OrderItemResponse> items,
        ShippingResponse shipping,
        PaymentResponse payment
) {
}
