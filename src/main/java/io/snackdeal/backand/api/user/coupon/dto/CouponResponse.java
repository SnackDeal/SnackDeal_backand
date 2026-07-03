package io.snackdeal.backand.api.user.coupon.dto;

import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;

import java.time.LocalDateTime;

/** 이벤트(발급 가능) 쿠폰 응답. */
public record CouponResponse(
        Long id,
        String name,
        DiscountType discountType,
        Long discountValue,
        Long minOrderPrice,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        IssueType issueType,
        Integer totalQuantity,
        Integer issuedQuantity
) {
}
