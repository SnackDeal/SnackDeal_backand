package io.snackdeal.backand.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    // HMAC-SHA 서명에는 충분히 긴(256bit 이상) 키가 필요하므로 테스트용 긴 문자열 사용
    private final JwtTokenProvider jwtTokenProvider =
            new JwtTokenProvider("testsecretkeytestsecretkeytestsecretkeytestsecretkeytestsecretkey");

    @Test
    @DisplayName("issue - 클레임을 담은 토큰을 발급하고, 그 토큰에서 클레임을 다시 읽을 수 있다")
    void issue_Success() {
        String token = jwtTokenProvider.issue(60_000L, Map.of("email", "a@test.com", "role", "USER"));

        assertNotNull(token);
        Map<String, Object> claims = jwtTokenProvider.getClaims(token);
        assertEquals("a@test.com", claims.get("email"));
        assertEquals("USER", claims.get("role"));
    }

    @Test
    @DisplayName("issueRefreshToken - email/type(refresh)/sid 클레임이 담긴다")
    void issueRefreshToken_Success() {
        String token = jwtTokenProvider.issueRefreshToken(60_000L, "a@test.com", "session-1");

        Map<String, Object> claims = jwtTokenProvider.getClaims(token);
        assertEquals("a@test.com", claims.get("email"));
        assertEquals("refresh", claims.get("type"));
        assertEquals("session-1", claims.get("sid"));
    }

    @Test
    @DisplayName("validate - 정상 토큰은 true")
    void validate_ValidToken() {
        String token = jwtTokenProvider.issue(60_000L, Map.of("email", "a@test.com"));
        assertTrue(jwtTokenProvider.validate(token));
    }

    @Test
    @DisplayName("validate - 손상된 토큰은 false")
    void validate_MalformedToken() {
        assertFalse(jwtTokenProvider.validate("not-a-jwt-token"));
    }

    @Test
    @DisplayName("validate - 만료된 토큰은 false")
    void validate_ExpiredToken() {
        // 유효시간을 음수로 주면 만료시각이 과거 → 즉시 만료 토큰
        String expired = jwtTokenProvider.issue(-1_000L, Map.of("email", "a@test.com"));
        assertFalse(jwtTokenProvider.validate(expired));
    }
}
