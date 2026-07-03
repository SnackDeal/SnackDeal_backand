package io.snackdeal.backand.api.user.delivery.dto;

import jakarta.validation.constraints.NotBlank;

/** 배송지 등록/수정 요청. */
public record DeliveryRequest(
        @NotBlank String name,
        @NotBlank String receiverName,
        @NotBlank String receiverPhone,
        @NotBlank String zipcode,
        @NotBlank String address,
        String detailAddress,
        boolean isDefault
) {
}
