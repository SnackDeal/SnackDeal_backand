package io.snackdeal.backand.api.admin.coupon.dto;

import java.time.LocalDateTime;

public record AdminCouponBoardResponse(
        Long id,
        String title,
        String content,
        String thumbnailUrl,
        boolean isActive,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime createdAt
) {
}
