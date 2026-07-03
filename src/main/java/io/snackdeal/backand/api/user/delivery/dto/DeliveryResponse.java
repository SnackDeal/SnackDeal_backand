package io.snackdeal.backand.api.user.delivery.dto;

/** 배송지 응답. */
public record DeliveryResponse(
        Long id,
        String name,
        String receiverName,
        String receiverPhone,
        String zipcode,
        String address,
        String detailAddress,
        boolean isDefault
) {
}
