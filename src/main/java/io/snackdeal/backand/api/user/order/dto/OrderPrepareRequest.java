package io.snackdeal.backand.api.user.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 주문 준비 요청.
 * deliveryId 를 주면 주소록에서 배송지를 채우고, 없으면 shipping 을 직접 사용한다.
 * (둘 중 하나는 있어야 하며, 최종 배송지 유효성은 서비스에서 검증한다.)
 */
public record OrderPrepareRequest(
        @NotEmpty List<@Valid OrderItemRequest> items,
        Long deliveryId,
        @Valid ShippingRequest shipping,
        Long userCouponId
) {
}
