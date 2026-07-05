package io.snackdeal.backand.api.user.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 주문 준비 요청.
 * deliveryId 를 주면 주소록에서 배송지를 채우고, 없으면 shipping 을 직접 사용한다.
 * (둘 중 하나는 있어야 하며, 최종 배송지 유효성은 서비스에서 검증한다.)
 */
@Schema(description = "주문 준비 요청")
public record OrderPrepareRequest(
        @Schema(description = "주문 상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty List<@Valid OrderItemRequest> items,

        @Schema(description = "주소록 배송지 ID (deliveryId 또는 shipping 중 하나 필수)", example = "1")
        Long deliveryId,

        @Schema(description = "직접 입력 배송지 (deliveryId 없을 때 필수)")
        @Valid ShippingRequest shipping,

        @Schema(description = "사용할 사용자 쿠폰 ID", example = "3")
        Long userCouponId
) {
}
