package io.snackdeal.backand.api.admin.main.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "일자별 건수 (Chart.js 라인/바 차트의 한 포인트)")
public record DailyCountPoint(
        @Schema(description = "날짜", example = "2026-07-09")
        LocalDate date,

        @Schema(description = "해당 날짜의 건수", example = "12")
        long count
) {
}
