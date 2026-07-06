package io.snackdeal.backand.api.user.delivery.dto;

import io.snackdeal.backand.domain.delivery.entity.Delivery;
import io.swagger.v3.oas.annotations.media.Schema;

/** 배송지 응답. */
@Schema(description = "배송지 응답")
public record DeliveryResponse(
        @Schema(description = "배송지 id", example = "5")
        Long id,

        @Schema(description = "배송지명", example = "우리집")
        String name,

        @Schema(description = "수령인 이름", example = "홍길동")
        String receiverName,

        @Schema(description = "수령인 연락처, 하이픈 포함 형식", example = "010-1234-5678")
        String receiverPhone,

        @Schema(description = "우편번호(Kakao/Daum Postcode zonecode)", example = "06133")
        String zipcode,

        @Schema(description = "기본 주소", example = "서울 강남구 테헤란로 123")
        String address,

        @Schema(description = "상세 주소", example = "101동 1203호")
        String detailAddress,

        @Schema(description = "기본 배송지 여부", example = "true")
        boolean isDefault
) {
    public static DeliveryResponse from(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getName(),
                delivery.getReceiverName(),
                delivery.getReceiverPhone(),
                delivery.getZipcode(),
                delivery.getAddress(),
                delivery.getDetailAddress(),
                delivery.isDefault()
        );
    }
}
