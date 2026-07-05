package io.snackdeal.backand.api.admin.order.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 관리자 환불 처리 요청.
 * approve=true 승인 / false 거절. 거절 시 rejectReason 필수(서비스에서 검증).
 * restoreStock 미지정 시 기본 true.
 */
public record AdminRefundRequest(
        @NotNull Boolean approve,
        String rejectReason,
        Boolean restoreStock
) {
    // 재고 복구 여부 (기본값 true).
    public boolean restoreStockOrDefault() {
        return restoreStock == null || restoreStock;
    }
}
