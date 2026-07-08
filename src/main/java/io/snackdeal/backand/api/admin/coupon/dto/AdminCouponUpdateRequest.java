package io.snackdeal.backand.api.admin.coupon.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AdminCouponUpdateRequest(
        @Size(max = 50)
        String name,

        LocalDateTime validUntil,

        Integer totalQuantity
) {
}
