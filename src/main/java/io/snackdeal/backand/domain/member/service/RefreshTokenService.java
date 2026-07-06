package io.snackdeal.backand.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * RefreshToken/세션을 Redis 에 보관하는 서비스.
 * DB 대신 Redis 를 쓰는 이유: 만료시간(TTL)을 키에 걸어 자동 만료시킬 수 있고, 로그아웃/탈퇴 시 즉시 삭제가 쉽다.
 * 키 구조:  refresh:{email} → RefreshToken,   session:{email} → 현재 활성 세션ID
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // RefreshToken 저장 키 접두사 (세션 키는 "session:" 사용)
    private static final String PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    // RefreshToken/세션의 TTL(ms) 이 시간이 지나면 Redis 에서 자동 삭제됨
    @Value("${custom.jwt.refresh-expiration}")
    private long refreshExpiration;

    // 로그인 시 RefreshToken 과 세션ID를 TTL 과 함께 저장 (재로그인하면 기존 값 덮어씀)
    public void save(String email, String refreshToken, String sessionId) {
        redisTemplate.opsForValue().set(PREFIX + email, refreshToken, Duration.ofMillis(refreshExpiration));
        redisTemplate.opsForValue().set("session:" + email, sessionId, Duration.ofMillis(refreshExpiration));
    }

    // 저장된 RefreshToken 조회 (재발급 시 클라이언트가 보낸 토큰과 대조하는 용도)
    public Optional<String> find(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(PREFIX + email));
    }

    // 현재 활성 세션ID 조회 (다른 기기 로그인 감지 등)
    public Optional<String> getActiveSessionId(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("session:" + email));
    }

    // 로그아웃/탈퇴 시 RefreshToken 과 세션을 즉시 제거해 재발급을 막는다
    public void delete(String email) {
        redisTemplate.delete(PREFIX + email);
        redisTemplate.delete("session:" + email);
    }
}
