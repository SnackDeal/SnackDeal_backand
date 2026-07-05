package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** 관리자 주문 상태 변경 요청. memo 는 감사 로그용(선택). */
@Schema(description = "관리자 주문 상태 변경 요청")
public record AdminOrderStatusRequest(
        @Schema(description = "변경할 주문 상태. 지정 가능: PREPARING_SHIPMENT / SHIPPED / COMPLETED / CANCELLED",
                example = "PREPARING_SHIPMENT", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull OrderStatus status,

        @Schema(description = "감사 로그용 메모 (선택)", example = "출고 준비 완료")
        String memo
) {
}
