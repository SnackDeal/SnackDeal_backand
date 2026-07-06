package io.snackdeal.backand.api.user.coupon.dto;

import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;

import java.time.LocalDateTime;

/** 내가 보유한 쿠폰 응답 */
public record MyCouponResponse(
        Long userCouponId,
        Long couponId,
        String name,
        DiscountType discountType,
        Long discountValue,
        Long minOrderPrice,
        UserCouponStatus status,
        LocalDateTime issuedAt,
        LocalDateTime usedAt,
        LocalDateTime validUntil
) {
}
