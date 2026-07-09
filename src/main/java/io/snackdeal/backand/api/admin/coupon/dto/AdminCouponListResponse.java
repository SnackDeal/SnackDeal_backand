package io.snackdeal.backand.api.admin.coupon.dto;

import java.util.List;

public record AdminCouponListResponse(
        List<AdminCouponSummaryResponse> coupons,
        int page,
        int size,
        long total
) {
}
