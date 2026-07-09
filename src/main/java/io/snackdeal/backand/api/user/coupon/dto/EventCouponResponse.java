package io.snackdeal.backand.api.user.coupon.dto;

import io.snackdeal.backand.domain.coupon.entity.DiscountType;

import java.time.LocalDateTime;

public record EventCouponResponse(
        Long id,
        String name,
        DiscountType discountType,
        Long discountValue,
        Long minOrderPrice,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        Integer remainingQuantity,
        String state,
        boolean alreadyDownloaded
) {
}
