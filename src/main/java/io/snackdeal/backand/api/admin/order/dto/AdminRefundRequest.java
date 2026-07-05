package io.snackdeal.backand.api.admin.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 관리자 환불 처리 요청.
 * approve=true 승인 / false 거절. 거절 시 rejectReason 필수(서비스에서 검증).
 * restoreStock 미지정 시 기본 true.
 */
@Schema(description = "관리자 환불 처리 요청")
public record AdminRefundRequest(
        @Schema(description = "승인(true) 또는 거절(false)", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Boolean approve,

        @Schema(description = "거절 사유 (approve=false 시 필수)", example = "이미 배송이 시작되었습니다")
        String rejectReason,

        @Schema(description = "재고 복구 여부 (미지정 시 기본 true)", example = "true")
        Boolean restoreStock
) {
    // 재고 복구 여부 (기본값 true).
    public boolean restoreStockOrDefault() {
        return restoreStock == null || restoreStock;
    }
}
