package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/** 환불 요청 접수 응답 실제 승인/거절은 관리자 환불처리 API 에서 이뤄진다 */
@Schema(description = "환불 요청 접수 응답")
public record RefundResponse(
        @Schema(description = "주문 ID", example = "123") Long orderId,
        @Schema(description = "주문번호", example = "ORD-20260705-00123") String orderNumber,
        @Schema(description = "주문 상태 (환불 요청 접수 시 REFUND_REQUESTED)", example = "REFUND_REQUESTED") OrderStatus status
) {
}
