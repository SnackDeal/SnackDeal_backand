package io.snackdeal.backand.api.admin.order.dto;

import jakarta.validation.constraints.PositiveOrZero;

/** 배송비 정책 변경 요청. 각 항목은 선택값이며 null 이면 기존 값을 유지한다(부분 수정). */
public record ShippingPolicyUpdateRequest(
        @PositiveOrZero Long baseFee,
        @PositiveOrZero Long freeThreshold
) {
}
