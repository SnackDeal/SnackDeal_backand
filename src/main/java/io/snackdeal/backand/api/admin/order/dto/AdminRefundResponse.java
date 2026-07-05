package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.snackdeal.backand.domain.order.entity.PaymentStatus;

import java.time.LocalDateTime;

/**
 * 관리자 환불 처리 결과.
 * 승인 시: status=REFUND_COMPLETED, cancelledAt/paymentStatus/재고·쿠폰 복구 여부를 채운다.
 * 거절 시: status=요청 이전 상태, refundRejectedAt/rejectReason 을 채운다.
 * 각 케이스에서 무관한 필드는 null 로 내려간다.
 */
public record AdminRefundResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        LocalDateTime cancelledAt,
        PaymentStatus paymentStatus,
        Boolean stockRestored,
        Boolean couponRestored,
        LocalDateTime refundRejectedAt,
        String rejectReason
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
