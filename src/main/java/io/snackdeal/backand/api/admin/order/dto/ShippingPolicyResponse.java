package io.snackdeal.backand.api.admin.order.dto;

import java.time.LocalDateTime;

/** 배송비 정책 응답. freeThreshold 이상 주문은 무료, 미만은 baseFee 부과. */
public record ShippingPolicyResponse(
        Long baseFee,
        Long freeThreshold,
        LocalDateTime updatedAt
) {
}
