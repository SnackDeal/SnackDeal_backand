package io.snackdeal.backand.api.admin.main.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 관리자 대시보드 요약 응답.
 * 모든 값은 다른 도메인 테이블의 집계(파생)이며, DashboardService 에서 계산한다.
 */
@Schema(description = "관리자 대시보드 요약 지표")
public record DashboardResponse(
        @Schema(description = "오늘 들어온 주문 수", example = "12")
        long todayOrderCount,

        @Schema(description = "오늘 매출 합계(취소/환불 제외)", example = "340000")
        long todaySalesAmount,

        @Schema(description = "오늘 가입한 회원 수", example = "5")
        long newMemberCount,

        @Schema(description = "재고 임계치 이하 상품 수", example = "3")
        long lowStockProductCount,

        @Schema(description = "답변 대기중인 문의 수", example = "6")
        long pendingQnaCount
) {
}
