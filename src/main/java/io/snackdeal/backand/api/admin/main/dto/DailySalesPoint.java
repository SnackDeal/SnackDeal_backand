package io.snackdeal.backand.api.admin.main.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "일자별 상품판매 (매출액/판매수량, 취소·환불 제외)")
public record DailySalesPoint(
        @Schema(description = "날짜", example = "2026-07-09")
        LocalDate date,

        @Schema(description = "해당 날짜의 매출액 합계", example = "340000")
        long salesAmount,

        @Schema(description = "해당 날짜의 판매수량 합계", example = "42")
        long soldQuantity
) {
}
