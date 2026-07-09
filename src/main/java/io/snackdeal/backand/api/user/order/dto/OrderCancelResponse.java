package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/** 결제 대기 주문 즉시취소 응답 */
@Schema(description = "주문 취소 응답")
public record OrderCancelResponse(
        @Schema(description = "주문 ID", example = "123") Long orderId,
        @Schema(description = "주문번호", example = "ORD-20260705-00123") String orderNumber,
        @Schema(description = "주문 상태 (취소 시 CANCELLED)", example = "CANCELLED") OrderStatus status
) {
}
