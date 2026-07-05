package io.snackdeal.backand.api.user.order.dto;

import jakarta.validation.constraints.NotBlank;

/** 배송지 입력. 주소록(delivery)에서 선택하거나 직접 입력한다. */
public record ShippingRequest(
        @NotBlank String receiverName,
        @NotBlank String receiverPhone,
        @NotBlank String zipcode,
        @NotBlank String address,
        String detailAddress,
        String deliveryRequest
) {
}
