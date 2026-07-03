package io.snackdeal.backand.api.user.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** 주문 항목 요청. */
public record OrderItemRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity
) {
}
