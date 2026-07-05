package io.snackdeal.backand.domain.order.client;

import io.snackdeal.backand.domain.order.client.dto.PortOnePayment;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 포트원 V2(api.portone.io) 실제 API 연동 구현체.
 *
 * 인증: 별도 토큰 발급 없이 `Authorization: PortOne {API_SECRET}` 헤더를 그대로 사용한다.
 * API Secret 은 코드에 하드코딩하지 않고 환경변수(custom.portone.api-secret)로 주입받으며,
 * 값이 비어 있어도(로컬/테스트) 빈 생성은 되고 실제 호출 시에만 포트원과 통신한다.
 */
@Component
public class PortOneClientImpl implements PortOneClient {

    private final RestClient restClient;
    private final String authorization;

    public PortOneClientImpl(
            @Value("${custom.portone.api-url:https://api.portone.io}") String apiUrl,
            @Value("${custom.portone.api-secret:}") String apiSecret) {
        this.restClient = RestClient.builder().baseUrl(apiUrl).build();
        this.authorization = "PortOne " + apiSecret;
    }

    @Override
    public PortOnePayment getPayment(String paymentId) {
        try {
            Map<String, Object> body = restClient.get()
                    .uri("/payments/{paymentId}", paymentId)
                    .header("Authorization", authorization)
                    .retrieve()
                    .body(Map.class);
            if (body == null) {
                throw new BusinessException(ResponseCode.PAYMENT_VERIFICATION_FAILED);
            }
            return toPayment(body);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    @Override
    public void cancelPayment(String paymentId, String reason) {
        try {
            restClient.post()
                    .uri("/payments/{paymentId}/cancel", paymentId)
                    .header("Authorization", authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("reason", reason == null ? "" : reason))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            // 취소 실패는 검증 실패로 취급한다 (호출부에서 주문 실패 처리).
            throw new BusinessException(ResponseCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    /*
     * V2 결제 객체에서 검증에 필요한 값만 추출한다.
     *  - amount.total: 결제 총액
     *  - method.type: 결제수단(PaymentMethodCard → Card 로 접두어 제거)
     *  - channel.pgProvider: PG사(TOSSPAYMENTS 등)
     *  - paidAt: RFC3339 문자열
     */
    @SuppressWarnings("unchecked")
    private PortOnePayment toPayment(Map<String, Object> body) {
        Map<String, Object> amount = (Map<String, Object>) body.get("amount");
        Map<String, Object> method = (Map<String, Object>) body.get("method");
        Map<String, Object> channel = (Map<String, Object>) body.get("channel");

        return new PortOnePayment(
                asString(body.get("id")),
                amount == null ? null : asLong(amount.get("total")),
                asString(body.get("status")),
                payMethodOf(method),
                channel == null ? null : asString(channel.get("pgProvider")),
                asString(body.get("receiptUrl")),
                asDateTime(body.get("paidAt")));
    }

    private String payMethodOf(Map<String, Object> method) {
        if (method == null) {
            return null;
        }
        String type = asString(method.get("type"));
        // "PaymentMethodCard" → "Card" 처럼 접두어를 제거해 읽기 쉽게 만든다.
        return (type != null && type.startsWith("PaymentMethod")) ? type.substring("PaymentMethod".length()) : type;
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Long asLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    // 포트원 V2 paidAt 은 RFC3339 문자열(예: 2026-07-01T14:32:00+09:00).
    private LocalDateTime asDateTime(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return OffsetDateTime.parse(value.toString()).toLocalDateTime();
    }
}
