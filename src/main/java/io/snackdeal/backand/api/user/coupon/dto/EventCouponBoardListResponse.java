package io.snackdeal.backand.api.user.coupon.dto;

import java.util.List;

public record EventCouponBoardListResponse(
        List<EventCouponBoardSummaryResponse> boards
) {
}
