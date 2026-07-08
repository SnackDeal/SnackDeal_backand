package io.snackdeal.backand.api.user.coupon.dto;

import java.time.LocalDateTime;

public record EventCouponBoardSummaryResponse(
        Long id,
        String title,
        String thumbnailUrl,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
