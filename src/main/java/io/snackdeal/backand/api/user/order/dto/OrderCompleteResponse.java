package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/** 결제 검증 성공 → 주문 확정 응답. */
@Schema(description = "결제 확정 응답")
public record OrderCompleteResponse(
        @Schema(description = "주문 ID", example = "123") Long orderId,
        @Schema(description = "주문번호", example = "ORD-20260705-00123") String orderNumber,
        @Schema(description = "주문 상태", example = "PAYMENT_COMPLETED") OrderStatus status,
        @Schema(description = "상품 총액", example = "9000") Long productAmount,
        @Schema(description = "배송비", example = "0") Long shippingFee,
        @Schema(description = "할인 금액", example = "0") Long discountAmount,
        @Schema(description = "최종 결제 금액", example = "9000") Long finalAmount,
        @Schema(description = "결제 요약") Payment payment,
        @Schema(description = "결제 완료 일시", example = "2026-07-05T14:32:00") LocalDateTime paidAt
) {
    /** 확정된 결제 요약. */
    @Schema(description = "결제 요약")
    public record Payment(
            @Schema(description = "포트원 paymentId", example = "ORD-20260705-00123") String paymentId,
            @Schema(description = "결제수단", example = "Card") String payMethod,
            @Schema(description = "PG사", example = "TOSSPAYMENTS") String pgProvider,
            @Schema(description = "결제 상태", example = "PAID") PaymentStatus status,
            @Schema(description = "영수증 URL", example = "https://...") String receiptUrl
    ) {
    }
}
