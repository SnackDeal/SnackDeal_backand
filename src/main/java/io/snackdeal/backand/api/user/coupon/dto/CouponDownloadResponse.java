package io.snackdeal.backand.api.user.coupon.dto;

import io.snackdeal.backand.domain.coupon.entity.UserCouponStatus;

import java.time.LocalDateTime;

public record CouponDownloadResponse(
        Long userCouponId,
        Long couponId,
        String name,
        UserCouponStatus status,
        LocalDateTime issuedAt
) {
}
