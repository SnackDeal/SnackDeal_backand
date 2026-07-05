package io.snackdeal.backand.api.admin.order.dto;

import java.util.List;

/** 관리자 주문 목록 응답 (페이징 메타 포함). page 는 1-base. */
public record AdminOrderListResponse(
        List<AdminOrderSummaryResponse> orders,
        int page,
        int size,
        long total
) {
}
