package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/** 주문 상세의 결제 정보 응답 (금액 내역 + 결제수단) */
@Schema(description = "결제 정보")
public record PaymentResponse(
        @Schema(description = "상품 총액", example = "9000") Long productAmount,
        @Schema(description = "배송비", example = "0") Long shippingFee,
        @Schema(description = "사용 쿠폰명", example = "신규가입 10% 할인") String couponName,
        @Schema(description = "할인 금액", example = "0") Long discountAmount,
        @Schema(description = "최종 결제 금액", example = "9000") Long finalAmount,
        @Schema(description = "결제수단", example = "Card") String payMethod,
        @Schema(description = "PG사", example = "TOSSPAYMENTS") String pgProvider,
        @Schema(description = "결제 상태 (READY/PAID/FAILED/CANCELLED)", example = "PAID") PaymentStatus status,
        @Schema(description = "영수증 URL", example = "https://...") String receiptUrl,
        @Schema(description = "결제 완료 일시", example = "2026-07-05T14:32:00") LocalDateTime paidAt
) {
}
