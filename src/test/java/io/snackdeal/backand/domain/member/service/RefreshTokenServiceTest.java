package io.snackdeal.backand.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private static final String EMAIL = "hong@test.com";
    private static final long REFRESH_EXPIRATION = 604_800_000L;

    @Test
    @DisplayName("save - RefreshToken과 세션ID를 만료시간과 함께 저장")
    void save_Success() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpiration", REFRESH_EXPIRATION);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        refreshTokenService.save(EMAIL, "refresh-token", "session-1");

        Duration ttl = Duration.ofMillis(REFRESH_EXPIRATION);
        verify(valueOperations).set("refresh:" + EMAIL, "refresh-token", ttl);
        verify(valueOperations).set("session:" + EMAIL, "session-1", ttl);
    }

    @Test
    @DisplayName("find - 저장된 RefreshToken을 Optional로 반환")
    void find_Success() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh:" + EMAIL)).thenReturn("refresh-token");

        Optional<String> result = refreshTokenService.find(EMAIL);

        assertTrue(result.isPresent());
        assertEquals("refresh-token", result.get());
    }

    @Test
    @DisplayName("getActiveSessionId - 저장된 세션ID를 Optional로 반환")
    void getActiveSessionId_Success() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("session:" + EMAIL)).thenReturn("session-1");

        Optional<String> result = refreshTokenService.getActiveSessionId(EMAIL);

        assertTrue(result.isPresent());
        assertEquals("session-1", result.get());
    }

    @Test
    @DisplayName("delete - RefreshToken과 세션 키를 모두 삭제")
    void delete_Success() {
        refreshTokenService.delete(EMAIL);

        verify(redisTemplate).delete("refresh:" + EMAIL);
        verify(redisTemplate).delete("session:" + EMAIL);
    }
}
