package io.snackdeal.backand.api.admin.main.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "기간별 신규 회원가입 추이 (Chart.js 라인차트용)")
public record MemberChartResponse(
        @Schema(description = "일자별 신규 회원 수")
        List<DailyCountPoint> items
) {
}
