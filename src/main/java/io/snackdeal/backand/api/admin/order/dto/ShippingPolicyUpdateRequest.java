package io.snackdeal.backand.api.admin.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

/** 배송비 정책 변경 요청. 각 항목은 선택값이며 null 이면 기존 값을 유지한다(부분 수정). */
@Schema(description = "배송비 정책 변경 요청 (null 항목은 기존 값 유지)")
public record ShippingPolicyUpdateRequest(
        @Schema(description = "기본 배송비 (0 이상, null이면 유지)", example = "2500")
        @PositiveOrZero Long baseFee,

        @Schema(description = "무료 배송 기준 금액 (0 이상, null이면 유지)", example = "30000")
        @PositiveOrZero Long freeThreshold
) {
}
