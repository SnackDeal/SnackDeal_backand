package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;

/** 관리자 주문 상태 변경 결과. 이 API 로 변경하면 manualOverride 가 true 가 된다. */
public record AdminOrderStatusResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        boolean manualOverride,
        LocalDateTime updatedAt
) {
}
