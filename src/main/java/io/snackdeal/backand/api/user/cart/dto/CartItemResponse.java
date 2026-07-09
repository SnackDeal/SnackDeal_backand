package io.snackdeal.backand.api.user.cart.dto;

/** 장바구니 항목 응답 */
public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        Long price,
        Integer quantity
) {
}
