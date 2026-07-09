package io.snackdeal.backand.api.user.cart.dto;

import java.util.List;

/** 장바구니 전체 응답 */
public record CartResponse(
        List<CartItemResponse> items,
        Long totalPrice
) {
}
