package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 관리자 환불 처리 결과.
 * 승인 시: status=REFUND_COMPLETED, cancelledAt/paymentStatus/재고·쿠폰 복구 여부를 채운다.
 * 거절 시: status=요청 이전 상태, refundRejectedAt/rejectReason 을 채운다.
 * 각 케이스에서 무관한 필드는 null 로 내려간다.
 */
@Schema(description = "관리자 환불 처리 결과")
public record AdminRefundResponse(
        @Schema(description = "주문 ID", example = "123") Long id,
        @Schema(description = "주문번호", example = "ORD-20260705-00123") String orderNumber,
        @Schema(description = "처리 후 주문 상태 (승인=REFUND_COMPLETED, 거절=요청 이전 상태)", example = "REFUND_COMPLETED") OrderStatus status,
        @Schema(description = "취소 일시 (승인 시)", example = "2026-07-05T16:00:00") LocalDateTime cancelledAt,
        @Schema(description = "결제 상태 (승인 시 CANCELLED)", example = "CANCELLED") PaymentStatus paymentStatus,
        @Schema(description = "재고 복구 여부 (승인 시)", example = "true") Boolean stockRestored,
        @Schema(description = "쿠폰 복구 여부 (승인 시, 유효기간 남은 경우만 true)", example = "false") Boolean couponRestored,
        @Schema(description = "환불 거절 일시 (거절 시)", example = "2026-07-05T16:00:00") LocalDateTime refundRejectedAt,
        @Schema(description = "거절 사유 (거절 시)", example = "이미 배송이 시작되었습니다") String rejectReason
) {
    // 승인 성공 응답.
    public static AdminRefundResponse approved(Long id, String orderNumber, LocalDateTime cancelledAt,
                                               boolean stockRestored, boolean couponRestored) {
        return new AdminRefundResponse(id, orderNumber, OrderStatus.REFUND_COMPLETED, cancelledAt,
                PaymentStatus.CANCELLED, stockRestored, couponRestored, null, null);
    }

    // 거절 성공 응답 (요청 이전 상태로 복귀).
    public static AdminRefundResponse rejected(Long id, String orderNumber, OrderStatus revertedStatus,
                                               LocalDateTime refundRejectedAt, String rejectReason) {
        return new AdminRefundResponse(id, orderNumber, revertedStatus, null, null,
                null, null, refundRejectedAt, rejectReason);
    }
}
