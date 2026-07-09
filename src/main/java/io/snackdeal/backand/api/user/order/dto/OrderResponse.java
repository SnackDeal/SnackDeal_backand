package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/** 주문 상세 응답 (상품별 내역 + 배송지 + 결제 정보) */
@Schema(description = "주문 상세 응답")
public record OrderResponse(
        @Schema(description = "주문 ID", example = "123") Long orderId,
        @Schema(description = "주문번호", example = "ORD-20260705-00123") String orderNumber,
        @Schema(description = "주문 일시", example = "2026-07-05T14:32:00") LocalDateTime orderedAt,
        @Schema(description = "주문 상태", example = "PAYMENT_COMPLETED") OrderStatus status,
        @Schema(description = "주문 상품 목록") List<OrderItemResponse> items,
        @Schema(description = "배송지 정보") ShippingResponse shipping,
        @Schema(description = "결제 정보") PaymentResponse payment
) {
}
