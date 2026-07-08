package io.snackdeal.backand.api.admin.coupon.dto;

import io.snackdeal.backand.domain.coupon.entity.CouponStatus;

import java.time.LocalDateTime;

public record AdminCouponStatusResponse(
        Long id,
        boolean isActive,
        CouponStatus status,
        LocalDateTime updatedAt
) {
}
