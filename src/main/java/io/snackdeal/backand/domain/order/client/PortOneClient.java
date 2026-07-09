package io.snackdeal.backand.domain.order.client;

import io.snackdeal.backand.domain.order.client.dto.PortOnePayment;

/**
 * 포트원 V2(api.portone.io) 결제 검증/취소 연동 인터페이스.
 * V2 는 토큰 발급 없이 API Secret 을 Authorization 헤더로 바로 사용하며, 결제는 paymentId(주문번호)로 식별
 * 서비스는 이 인터페이스에만 의존하므로, 단위 테스트에서는 구현체 대신 Mock 을 주입
 */
public interface PortOneClient {

    // paymentId 로 실제 결제 내역을 조회 (GET https://api.portone.io/payments/{paymentId})
    PortOnePayment getPayment(String paymentId);

    // 결제를 취소 금액 위변조 감지 등으로 확정 전 되돌릴 때 호출 (POST /payments/{paymentId}/cancel)
    void cancelPayment(String paymentId, String reason);
}
