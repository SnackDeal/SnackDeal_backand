package io.snackdeal.backand.api.user.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** 장바구니 수량 변경 요청. */
public record CartItemUpdateRequest(
        @NotNull @Positive Integer quantity
) {
}
