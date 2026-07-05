package io.snackdeal.backand.domain.order.client;

import io.snackdeal.backand.domain.order.client.dto.PortOnePayment;

/**
 * 포트원(아임포트 V1) 결제 검증/취소 연동 인터페이스.
 * 서비스는 이 인터페이스에만 의존하므로, 단위 테스트에서는 구현체 대신 Mock 을 주입한다.
 */
public interface PortOneClient {

    // imp_uid 로 실제 결제 내역을 조회한다. (GET https://api.iamport.kr/payments/{imp_uid})
    PortOnePayment getPayment(String impUid);

    // 결제를 취소한다. 금액 위변조 감지 등으로 확정 전 되돌릴 때 호출. (POST /payments/cancel)
    void cancelPayment(String impUid, String reason);
}
