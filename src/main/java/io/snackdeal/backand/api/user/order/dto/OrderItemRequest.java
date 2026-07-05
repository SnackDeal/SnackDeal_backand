package io.snackdeal.backand.api.user.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** 주문 항목 요청. */
@Schema(description = "주문 항목")
public record OrderItemRequest(
        @Schema(description = "상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Long productId,

        @Schema(description = "주문 수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Positive Integer quantity
) {
}
