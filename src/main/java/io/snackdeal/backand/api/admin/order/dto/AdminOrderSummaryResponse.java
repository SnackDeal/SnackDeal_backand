package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/** 관리자 주문 리스트 항목 구매자 정보(email/name)를 함께 노출 */
@Schema(description = "관리자 주문 목록 항목")
public record AdminOrderSummaryResponse(
        @Schema(description = "주문 ID", example = "123") Long orderId,
        @Schema(description = "주문번호", example = "ORD-20260705-00123") String orderNumber,
        @Schema(description = "구매자 이메일", example = "buyer@test.com") String buyerEmail,
        @Schema(description = "구매자 이름", example = "홍길동") String buyerName,
        @Schema(description = "대표 상품명", example = "허니버터 프레첼") String mainProductName,
        @Schema(description = "최종 결제 금액", example = "9000") Long finalAmount,
        @Schema(description = "주문 상태", example = "PAYMENT_COMPLETED") OrderStatus status,
        @Schema(description = "주문 일시", example = "2026-07-05T14:32:00") LocalDateTime orderedAt
) {
}
