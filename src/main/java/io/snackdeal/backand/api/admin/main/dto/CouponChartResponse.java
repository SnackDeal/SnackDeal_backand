package io.snackdeal.backand.api.admin.main.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "기간별 쿠폰 발급/사용 추이 (Chart.js 라인차트용)")
public record CouponChartResponse(
        @Schema(description = "일자별 쿠폰 발급/사용 건수")
        List<DailyCouponPoint> items
) {
}
