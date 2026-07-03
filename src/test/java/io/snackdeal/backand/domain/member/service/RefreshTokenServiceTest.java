package io.snackdeal.backand.domain.member.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("save - TODO")
    void save_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("find - TODO")
    void find_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("getActiveSessionId - TODO")
    void getActiveSessionId_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("delete - TODO")
    void delete_Success() {
        fail("not implemented");
    }

}