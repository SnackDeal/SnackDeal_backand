package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/** 관리자 주문 상태 변경 결과. 이 API 로 변경하면 manualOverride 가 true 가 된다. */
@Schema(description = "관리자 주문 상태 변경 결과")
public record AdminOrderStatusResponse(
        @Schema(description = "주문 ID", example = "123") Long id,
        @Schema(description = "주문번호", example = "ORD-20260705-00123") String orderNumber,
        @Schema(description = "변경된 주문 상태", example = "PREPARING_SHIPMENT") OrderStatus status,
        @Schema(description = "관리자 수동 변경 여부 (이 API 사용 시 항상 true)", example = "true") boolean manualOverride,
        @Schema(description = "변경 일시", example = "2026-07-05T15:00:00") LocalDateTime updatedAt
) {
}
