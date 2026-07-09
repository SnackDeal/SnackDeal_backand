package io.snackdeal.backand.api.admin.order.dto;

import io.snackdeal.backand.domain.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** 관리자 주문 상태 변경 요청 SHIPPED 전환 시 courier/trackingNumber 입력 가능 */
@Schema(description = "관리자 주문 상태 변경 요청")
public record AdminOrderStatusRequest(
        @Schema(description = "변경할 주문 상태 지정 가능: PREPARING_SHIPMENT / SHIPPED / COMPLETED / CANCELLED",
                example = "PREPARING_SHIPMENT", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull OrderStatus status,

        @Schema(description = "택배사 (SHIPPED 전환 시 입력)", example = "CJ대한통운")
        String courier,

        @Schema(description = "송장번호 (SHIPPED 전환 시 입력)", example = "123456789012")
        String trackingNumber,

        @Schema(description = "감사 로그용 메모 (선택)", example = "출고 준비 완료")
        String memo
) {
}
