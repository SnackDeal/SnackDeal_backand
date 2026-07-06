package io.snackdeal.backand.api.user.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배송지 등록 응답")
public record DeliveryCreateResponse(
        @Schema(description = "생성된 배송지 id", example = "5")
        Long id,

        @Schema(description = "생성된 배송지의 기본 배송지 여부", example = "true")
        boolean isDefault
) {
}
