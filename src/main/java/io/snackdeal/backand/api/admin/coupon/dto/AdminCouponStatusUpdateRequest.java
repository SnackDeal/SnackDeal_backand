package io.snackdeal.backand.api.admin.coupon.dto;

import jakarta.validation.constraints.NotNull;

public record AdminCouponStatusUpdateRequest(
        @NotNull
        Boolean isActive
) {
}
