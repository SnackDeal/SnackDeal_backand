package io.snackdeal.backand.api.admin.main.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "기간별 상품판매(매출액/판매수량) 추이 (Chart.js 라인차트용)")
public record ProductSalesChartResponse(
        @Schema(description = "일자별 매출액/판매수량")
        List<DailySalesPoint> items
) {
}
