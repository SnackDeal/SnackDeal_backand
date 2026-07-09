package io.snackdeal.backand.api.user.coupon.dto;

import java.time.LocalDateTime;

public record EventCouponBoardDetailResponse(
        Long id,
        String title,
        String content,
        String thumbnailUrl,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
