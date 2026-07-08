package io.snackdeal.backand.api.admin.coupon.dto;

import java.util.List;

public record AdminCouponBoardListResponse(
        List<AdminCouponBoardResponse> boards
) {
}
