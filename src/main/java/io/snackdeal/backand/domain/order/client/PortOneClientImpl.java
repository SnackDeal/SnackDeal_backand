package io.snackdeal.backand.domain.order.client;

import io.snackdeal.backand.domain.order.client.dto.PortOnePayment;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * 포트원(아임포트 V1) 실제 API 연동 구현체.
 *
 * 흐름: imp_key/imp_secret 로 access_token 발급 → 토큰으로 결제 조회/취소.
 * 인증정보는 코드에 하드코딩하지 않고 환경변수(custom.portone.*)로 주입받는다.
 * 키가 비어 있어도(로컬/테스트) 빈 생성은 되며, 실제 호출 시에만 포트원과 통신한다.
 */
@Component
public class PortOneClientImpl implements PortOneClient {

    private final RestClient restClient;
    private final String impKey;
    private final String impSecret;

    public PortOneClientImpl(
            @Value("${custom.portone.api-url:https://api.iamport.kr}") String apiUrl,
            @Value("${custom.portone.imp-key:}") String impKey,
            @Value("${custom.portone.imp-secret:}") String impSecret) {
        this.restClient = RestClient.builder().baseUrl(apiUrl).build();
        this.impKey = impKey;
        this.impSecret = impSecret;
    }

    @Override
    public PortOnePayment getPayment(String impUid) {
        String token = issueAccessToken();
        try {
            Map<String, Object> body = restClient.get()
                    .uri("/payments/{impUid}", impUid)
                    .header("Authorization", token)
                    .retrieve()
                    .body(Map.class);
            Map<String, Object> response = extractResponse(body);
            return toPayment(response);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    @Override
    public void cancelPayment(String impUid, String reason) {
        String token = issueAccessToken();
        try {
            restClient.post()
                    .uri("/payments/cancel")
                    .header("Authorization", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("imp_uid", impUid, "reason", reason == null ? "" : reason))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            // 취소 실패는 검증 실패로 취급한다 (호출부에서 주문 실패 처리).
            throw new BusinessException(ResponseCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    // imp_key/imp_secret 으로 access_token 을 발급받는다.
    private String issueAccessToken() {
        try {
            Map<String, Object> body = restClient.post()
                    .uri("/users/getToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("imp_key", impKey, "imp_secret", impSecret))
                    .retrieve()
                    .body(Map.class);
            Map<String, Object> response = extractResponse(body);
            Object token = response.get("access_token");
            if (token == null) {
                throw new BusinessException(ResponseCode.PAYMENT_VERIFICATION_FAILED);
            }
            return token.toString();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    // 포트원 응답 공통 포맷 { code, message, response: {...} } 에서 response 를 꺼낸다.
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractResponse(Map<String, Object> body) {
        if (body == null || body.get("response") == null) {
            throw new BusinessException(ResponseCode.PAYMENT_VERIFICATION_FAILED);
        }
        return (Map<String, Object>) body.get("response");
    }

    private PortOnePayment toPayment(Map<String, Object> response) {
        return new PortOnePayment(
                asString(response.get("imp_uid")),
                asLong(response.get("amount")),
                asString(response.get("status")),
                asString(response.get("pay_method")),
                asString(response.get("pg_provider")),
                asString(response.get("receipt_url")),
                asDateTime(response.get("paid_at"))
        );
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Long asLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    // 포트원의 paid_at 은 unix epoch(초). 0 이면 미결제 상태이므로 null.
    private LocalDateTime asDateTime(Object value) {
        if (value == null) {
            return null;
        }
        long epochSeconds = ((Number) value).longValue();
        if (epochSeconds <= 0) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
    }
}
