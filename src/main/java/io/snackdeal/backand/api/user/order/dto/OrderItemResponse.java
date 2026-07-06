package io.snackdeal.backand.api.user.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** 주문 항목 응답. lineTotal = price * quantity. */
@Schema(description = "주문 항목 응답")
public record OrderItemResponse(
        @Schema(description = "상품 ID", example = "1") Long productId,
        @Schema(description = "상품명 (주문 시점 스냅샷)", example = "허니버터 프레첼") String productName,
        @Schema(description = "단가", example = "4500") Long price,
        @Schema(description = "수량", example = "2") Integer quantity,
        @Schema(description = "항목 합계 (price * quantity)", example = "9000") Long lineTotal
) {
}
