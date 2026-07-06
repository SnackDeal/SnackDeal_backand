package io.snackdeal.backand.api.user.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** 배송지 입력. 주소록(delivery)에서 선택하거나 직접 입력한다. */
@Schema(description = "배송지 입력")
public record ShippingRequest(
        @Schema(description = "수령인 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String receiverName,

        @Schema(description = "수령인 휴대폰번호", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String receiverPhone,

        @Schema(description = "우편번호", example = "06133", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String zipcode,

        @Schema(description = "주소", example = "서울 강남구 테헤란로 123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String address,

        @Schema(description = "상세 주소", example = "456호")
        String detailAddress,

        @Schema(description = "배송 요청사항", example = "부재 시 문 앞")
        String deliveryRequest
) {
}
