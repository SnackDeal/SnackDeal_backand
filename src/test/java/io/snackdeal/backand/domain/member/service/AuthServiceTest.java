package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.domain.member.service.MemberService;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.member.service.JwtTokenProvider;
import io.snackdeal.backand.domain.member.service.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Disabled("TODO: implement")
    @Test
    @DisplayName("login - TODO")
    void login_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("adminLogin - TODO")
    void adminLogin_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("refresh - TODO")
    void refresh_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("logout - TODO")
    void logout_Success() {
        fail("not implemented");
    }

    @Disabled("TODO: implement")
    @Test
    @DisplayName("issueTokens - TODO")
    void issueTokens_Success() {
        fail("not implemented");
    }

}