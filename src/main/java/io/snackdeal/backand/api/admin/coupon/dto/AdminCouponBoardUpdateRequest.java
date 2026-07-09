package io.snackdeal.backand.api.admin.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AdminCouponBoardUpdateRequest(
        @NotBlank
        @Size(max = 100)
        String title,

        @NotBlank
        String content,

        String thumbnailUrl,

        Boolean isActive,

        @NotNull
        LocalDateTime startAt,

        LocalDateTime endAt
) {
}
