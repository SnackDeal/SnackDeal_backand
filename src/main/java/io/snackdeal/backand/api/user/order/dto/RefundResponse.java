package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;

/** 환불 요청 접수 응답. 실제 승인/거절은 관리자 환불처리 API 에서 이뤄진다. */
public record RefundResponse(
        Long orderId,
        String orderNumber,
        OrderStatus status
) {
}
