package io.snackdeal.backand.api.user.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** 주문 목록 응답 (페이징 메타 포함) page 는 1-base 로 내려준다 */
@Schema(description = "주문 목록 응답")
public record OrderListResponse(
        @Schema(description = "주문 목록") List<OrderSummaryResponse> orders,
        @Schema(description = "현재 페이지 (0-base)", example = "0") int page,
        @Schema(description = "페이지 크기", example = "10") int size,
        @Schema(description = "전체 주문 수", example = "1") long total
) {
}
