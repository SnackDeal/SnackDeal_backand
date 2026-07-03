package io.snackdeal.backand.api.user.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** 장바구니 담기 요청. */
public record CartItemAddRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity
) {
}
