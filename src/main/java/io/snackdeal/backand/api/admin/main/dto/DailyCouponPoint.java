package io.snackdeal.backand.api.admin.main.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "일자별 쿠폰 발급/사용 건수")
public record DailyCouponPoint(
        @Schema(description = "날짜", example = "2026-07-09")
        LocalDate date,

        @Schema(description = "해당 날짜의 쿠폰 발급 건수", example = "8")
        long issuedCount,

        @Schema(description = "해당 날짜의 쿠폰 사용 건수", example = "5")
        long usedCount
) {
}
