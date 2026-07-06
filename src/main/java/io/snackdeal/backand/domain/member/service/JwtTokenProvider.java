package io.snackdeal.backand.domain.member.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT(Json Web Token) 발급/검증 담당.
 * 대칭키(HMAC-SHA) 방식으로 서명하며, 서명 키(appKey)는 설정값에서 주입받는다.
 * - issue           : 클레임을 담아 토큰 생성
 * - validate        : 서명/만료 검증 (예외를 잡아 true/false 로 변환)
 * - getClaims       : 토큰에서 payload(클레임) 추출
 */
@Slf4j
@Service
public class JwtTokenProvider {

    // 토큰 서명/검증에 쓰는 비밀키 원문 설정(custom.jwt.secrets.app-key)에서 주입
    private final String appKey;

    public JwtTokenProvider(@Value("${custom.jwt.secrets.app-key}") String appKey) {
        this.appKey = appKey;
    }

    /*
     * 토큰 발급.
     * validateTime(ms) 후 만료되며, 전달된 claims(예: email, role, sid)를 payload 에 담는다.
     */
    public String issue(long validateTime, Map<String, Object> claims) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + validateTime))
                .signWith(getSecretKey());

        claims.forEach(jwtBuilder::claim);

        return jwtBuilder.compact();
    }

    /*
     * RefreshToken 발급 AccessToken 과 구분되도록 type="refresh" 클레임을 추가하고,
     * 세션 식별자(sid)를 넣어 다중 로그인/강제 로그아웃 관리에 활용
     */
    public String issueRefreshToken(long validateTime, String email, String sessionId) {
        return issue(validateTime, Map.of("email", email, "type", "refresh", "sid", sessionId));
    }

    /*
     * 토큰 유효성 검사.
     * 파싱 중 서명 위조/만료/형식 오류가 나면 예외가 발생하는데, 이를 로깅 후 false 로 변환
     * (호출부에서 try-catch 없이 boolean 으로 분기할 수 있게 하기 위함)
     */
    public boolean validate(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            log.info("잘못된 토큰이 검출되었습니다! 토큰 : {}, 오류 메세지 : {}", token, e.getMessage());
        } catch (IllegalStateException ie) {
            log.info("토큰이 없거나 토큰에 문제가 있습니다! 토큰 : {}, 오류 메세지 : {}", token, ie.getMessage());
        } catch (Exception ex) {
            log.info("토큰 유효성 검사 중 문제가 발생했습니다 토큰 : {}, 오류 메세지 : {}", token, ex.getMessage());
        }

        return false;
    }

    /*
     * 토큰에서 클레임(payload)을 꺼낸다 서명 검증에 실패하면 여기서 예외가 발생
     * (validate 는 이 메서드를 호출해 예외 여부로 유효성을 판단)
     */
    public Map<String, Object> getClaims(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(getSecretKey())
                .build();

        Jws<Claims> result = parser.parseSignedClaims(token);
        return result.getPayload();
    }

    // 비밀키 원문(appKey)을 HMAC-SHA 서명용 SecretKey 로 변환 (키 길이는 256bit 이상이어야 함)
    private @NonNull SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(appKey.getBytes());
    }
}
