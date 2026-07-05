package io.snackdeal.backand.domain.order.client.dto;

import java.time.LocalDateTime;

/**
 * 포트원(아임포트) 결제 조회 결과에서 검증에 필요한 값만 추린 응답.
 * status 는 포트원 원문("paid"/"cancelled"/"failed"/"ready") 그대로 담는다.
 */
public record PortOnePayment(
        String impUid,
        Long amount,
        String status,
        String payMethod,
        String pgProvider,
        String receiptUrl,
        LocalDateTime paidAt
) {
    // 실제 결제가 완료(paid)되었는지 여부.
    public boolean isPaid() {
        return "paid".equalsIgnoreCase(status);
    }
}
