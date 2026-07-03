package io.snackdeal.backand.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    @Value("${custom.jwt.refresh-expiration}")
    private long refreshExpiration;

    public void save(String email, String refreshToken, String sessionId) {
        redisTemplate.opsForValue().set(PREFIX + email, refreshToken, Duration.ofMillis(refreshExpiration));
        redisTemplate.opsForValue().set("session:" + email, sessionId, Duration.ofMillis(refreshExpiration));
    }

    public Optional<String> find(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(PREFIX + email));
    }

    public Optional<String> getActiveSessionId(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("session:" + email));
    }

    public void delete(String email) {
        redisTemplate.delete(PREFIX + email);
        redisTemplate.delete("session:" + email);
    }
}
