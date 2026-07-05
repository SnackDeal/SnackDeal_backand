package io.snackdeal.backand.api.user.order.dto;

import java.util.List;

/** 주문 목록 응답 (페이징 메타 포함). page 는 1-base 로 내려준다. */
public record OrderListResponse(
        List<OrderSummaryResponse> orders,
        int page,
        int size,
        long total
) {
}
