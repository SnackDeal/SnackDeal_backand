package io.snackdeal.backand.api.admin.coupon.dto;

import io.snackdeal.backand.domain.coupon.entity.CouponStatus;
import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;

import java.time.LocalDateTime;

public record AdminCouponSummaryResponse(
        Long id,
        String name,
        DiscountType discountType,
        Long discountValue,
        Long minOrderPrice,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        IssueType issueType,
        Long couponBoardId,
        String couponBoardTitle,
        Integer totalQuantity,
        Integer issuedQuantity,
        long usedCount,
        boolean isActive,
        CouponStatus status
) {
}
