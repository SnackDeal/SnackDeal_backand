package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

/** 관리자 주문 상태 변경 요청. memo 는 감사 로그용(선택). */
public record AdminOrderStatusRequest(
        @NotNull OrderStatus status,
        String memo
) {
}
