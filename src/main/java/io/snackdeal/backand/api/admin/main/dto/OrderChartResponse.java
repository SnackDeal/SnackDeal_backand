package io.snackdeal.backand.api.admin.main.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "기간별 주문 수 추이 (Chart.js 라인차트용)")
public record OrderChartResponse(
        @Schema(description = "일자별 주문 수")
        List<DailyCountPoint> items
) {
}
