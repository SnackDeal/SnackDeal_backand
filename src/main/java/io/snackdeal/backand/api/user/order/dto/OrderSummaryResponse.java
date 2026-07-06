package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/** 주문 목록 항목 응답. mainProductName = 대표 상품명, itemCount = 주문 상품 종류 수. */
@Schema(description = "주문 목록 항목")
public record OrderSummaryResponse(
        @Schema(description = "주문 ID", example = "123") Long orderId,
        @Schema(description = "주문번호", example = "ORD-20260705-00123") String orderNumber,
        @Schema(description = "주문 일시", example = "2026-07-05T14:32:00") LocalDateTime orderedAt,
        @Schema(description = "대표 상품명", example = "허니버터 프레첼") String mainProductName,
        @Schema(description = "주문 상품 종류 수", example = "1") int itemCount,
        @Schema(description = "최종 결제 금액", example = "9000") Long finalAmount,
        @Schema(description = "주문 상태", example = "PAYMENT_COMPLETED") OrderStatus status
) {
}
