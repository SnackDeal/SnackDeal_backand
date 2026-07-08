package io.snackdeal.backand.api.admin.coupon.dto;

import io.snackdeal.backand.domain.coupon.entity.DiscountType;
import io.snackdeal.backand.domain.coupon.entity.IssueType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AdminCouponCreateRequest(
        @NotBlank
        @Size(max = 50)
        String name,

        @NotNull
        DiscountType discountType,

        @NotNull
        Long discountValue,

        @Min(0)
        Long minOrderPrice,

        @NotNull
        LocalDateTime validFrom,

        LocalDateTime validUntil,

        @Min(0)
        Integer totalQuantity,

        @NotNull
        IssueType issueType,

        Long couponBoardId,

        Boolean isActive
) {
}
