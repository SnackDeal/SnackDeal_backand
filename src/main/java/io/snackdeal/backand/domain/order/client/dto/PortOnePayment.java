package io.snackdeal.backand.domain.order.client.dto;

import java.time.LocalDateTime;

/**
 * 포트원 V2 결제 조회 결과에서 검증에 필요한 값만 추린 응답.
 * status 는 포트원 V2 원문("PAID"/"CANCELLED"/"FAILED"/"READY" 등) 그대로 담는다.
 * amount 는 결제 총액(amount.total).
 */
public record PortOnePayment(
        String paymentId,
        Long amount,
        String status,
        String payMethod,
        String pgProvider,
        String receiptUrl,
        LocalDateTime paidAt
) {
    // 실제 결제가 완료(PAID)되었는지 여부 (대소문자 무시)
    public boolean isPaid() {
        return "PAID".equalsIgnoreCase(status);
    }
}
