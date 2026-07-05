package io.snackdeal.backand.api.user.order.dto;

/** 주문 항목 응답. lineTotal = price * quantity. */
public record OrderItemResponse(
        Long productId,
        String productName,
        Long price,
        Integer quantity,
        Long lineTotal
) {
}
