package io.snackdeal.backand.api.admin.main.controller;

import io.snackdeal.backand.api.user.member.dto.LoginRequest;
import io.snackdeal.backand.api.user.member.dto.TokenResponse;
import io.snackdeal.backand.domain.member.service.AuthService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 관리자 로그인 컨트롤러 단위테스트: AuthService.adminLogin 위임만 검증
 */
@ExtendWith(MockitoExtension.class)
class AdminAuthControllerTest {

    @InjectMocks
    private AdminAuthController adminAuthController;

    @Mock
    private AuthService authService;

    @Test
    @DisplayName("login - adminLogin 결과(토큰)를 그대로 감싸 반환")
    void login() {
        LoginRequest request = new LoginRequest("admin@test.com", "p@ssW0rd!");
        TokenResponse expected = new TokenResponse("access-token", "refresh-token");
        when(authService.adminLogin(request)).thenReturn(expected);

        CommonResponse<TokenResponse> response = adminAuthController.login(request);

        assertTrue(response.isSuccess());
        assertSame(expected, response.getData());
        verify(authService).adminLogin(request);
    }
}
