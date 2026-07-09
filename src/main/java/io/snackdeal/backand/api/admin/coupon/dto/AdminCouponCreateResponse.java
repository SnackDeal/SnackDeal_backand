package io.snackdeal.backand.api.admin.coupon.dto;

import io.snackdeal.backand.domain.coupon.entity.CouponStatus;

import java.time.LocalDateTime;

public record AdminCouponCreateResponse(
        Long id,
        String name,
        boolean isActive,
        CouponStatus status,
        Long couponBoardId,
        LocalDateTime createdAt
) {
}
