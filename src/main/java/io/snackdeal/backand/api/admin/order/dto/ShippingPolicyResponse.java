package io.snackdeal.backand.api.admin.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/** 배송비 정책 응답. freeThreshold 이상 주문은 무료, 미만은 baseFee 부과. */
@Schema(description = "배송비 정책 응답")
public record ShippingPolicyResponse(
        @Schema(description = "기본 배송비 (무료기준 미만 주문에 부과)", example = "0") Long baseFee,
        @Schema(description = "무료 배송 기준 금액 (이상이면 배송비 0원)", example = "20000") Long freeThreshold,
        @Schema(description = "정책 최종 변경 일시", example = "2026-07-05T11:00:00") LocalDateTime updatedAt
) {
}
