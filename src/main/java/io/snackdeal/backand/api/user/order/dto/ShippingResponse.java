package io.snackdeal.backand.api.user.order.dto;

import io.snackdeal.backand.domain.order.entity.ShippingStatus;

/** 주문 상세의 배송지/배송상태 응답. */
public record ShippingResponse(
        String receiverName,
        String receiverPhone,
        String zipcode,
        String address,
        String detailAddress,
        String deliveryRequest,
        String courier,
        String trackingNumber,
        ShippingStatus status
) {
}
