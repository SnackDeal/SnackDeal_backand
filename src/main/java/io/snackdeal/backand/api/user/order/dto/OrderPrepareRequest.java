package io.snackdeal.backand.api.user.order.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** 주문 준비 요청. */
public record OrderPrepareRequest(
        @NotEmpty List<OrderItemRequest> items,
        Long userCouponId,
        Long deliveryId
) {
}
