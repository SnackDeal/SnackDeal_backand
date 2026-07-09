package io.snackdeal.backand.api.user.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "내 배송지 목록 응답")
public record DeliveryListResponse(
        @Schema(description = "삭제되지 않은 내 배송지 목록")
        List<DeliveryResponse> deliveries
) {
}
