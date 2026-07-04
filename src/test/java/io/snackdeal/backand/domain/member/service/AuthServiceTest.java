package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.api.user.member.dto.LoginRequest;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.member.dto.TokenResponse;
import io.snackdeal.backand.domain.member.entity.Gender;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.entity.MemberStatus;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private static final String EMAIL = "hong@test.com";
    private static final String RAW_PASSWORD = "p@ssW0rd!";

    @BeforeEach
    void setUp() {
        // @Value 로 주입되는 만료시간은 토큰 발급 시 사용되므로 임의값을 넣어준다
        ReflectionTestUtils.setField(authService, "accessExpiration", 900_000L);
        ReflectionTestUtils.setField(authService, "refreshExpiration", 604_800_000L);
    }

    private Member member(MemberRole role, MemberStatus status) {
        Member m = Member.builder()
                .email(EMAIL).password("ENCODED").name("홍길동")
                .birth(LocalDate.of(2000, 1, 1)).gender(Gender.MALE)
                .phone("01011112222").role(role)
                .build();
        if (status != MemberStatus.ACTIVE) {
            m.changeStatus(status);
        }
        return m;
    }

    private void stubTokenIssue() {
        when(jwtTokenProvider.issue(anyLong(), anyMap())).thenReturn("access-token");
        when(jwtTokenProvider.issueRefreshToken(anyLong(), eq(EMAIL), anyString())).thenReturn("refresh-token");
    }

    @Test
    @DisplayName("login - 이메일/비밀번호가 맞으면 access/refresh 토큰을 발급한다")
    void login_Success() {
        MemberDetails details = new MemberDetails(1L, EMAIL, "ENCODED", MemberRole.USER);
        when(memberService.loadUserByUsername(EMAIL)).thenReturn(details);
        when(passwordEncoder.matches(RAW_PASSWORD, "ENCODED")).thenReturn(true);
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(MemberRole.USER, MemberStatus.ACTIVE)));
        stubTokenIssue();

        TokenResponse tokens = authService.login(new LoginRequest(EMAIL, RAW_PASSWORD));

        assertEquals("access-token", tokens.accessToken());
        assertEquals("refresh-token", tokens.refreshToken());
        verify(refreshTokenService).save(eq(EMAIL), eq("refresh-token"), anyString());
    }

    @Test
    @DisplayName("login - 비밀번호가 틀리면 401(INVALID_PASSWORD)")
    void login_WrongPassword() {
        MemberDetails details = new MemberDetails(1L, EMAIL, "ENCODED", MemberRole.USER);
        when(memberService.loadUserByUsername(EMAIL)).thenReturn(details);
        when(passwordEncoder.matches(RAW_PASSWORD, "ENCODED")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(new LoginRequest(EMAIL, RAW_PASSWORD)));

        assertEquals(ResponseCode.INVALID_PASSWORD, ex.getResponseCode());
    }

    @Test
    @DisplayName("login - 탈퇴(DELETED) 계정은 로그인 차단(ACCOUNT_DELETED)")
    void login_DeletedAccount() {
        MemberDetails details = new MemberDetails(1L, EMAIL, "ENCODED", MemberRole.USER);
        when(memberService.loadUserByUsername(EMAIL)).thenReturn(details);
        when(passwordEncoder.matches(RAW_PASSWORD, "ENCODED")).thenReturn(true);
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(MemberRole.USER, MemberStatus.DELETED)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(new LoginRequest(EMAIL, RAW_PASSWORD)));

        assertEquals(ResponseCode.ACCOUNT_DELETED, ex.getResponseCode());
    }

    @Test
    @DisplayName("adminLogin - 관리자(ADMIN) 계정이면 토큰을 발급한다")
    void adminLogin_Success() {
        MemberDetails details = new MemberDetails(1L, EMAIL, "ENCODED", MemberRole.ADMIN);
        when(memberService.loadUserByUsername(EMAIL)).thenReturn(details);
        when(passwordEncoder.matches(RAW_PASSWORD, "ENCODED")).thenReturn(true);
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(MemberRole.ADMIN, MemberStatus.ACTIVE)));
        stubTokenIssue();

        TokenResponse tokens = authService.adminLogin(new LoginRequest(EMAIL, RAW_PASSWORD));

        assertEquals("access-token", tokens.accessToken());
    }

    @Test
    @DisplayName("adminLogin - 관리자가 아니면 403(FORBIDDEN_ACCESS)")
    void adminLogin_NotAdmin() {
        MemberDetails details = new MemberDetails(1L, EMAIL, "ENCODED", MemberRole.USER);
        when(memberService.loadUserByUsername(EMAIL)).thenReturn(details);
        when(passwordEncoder.matches(RAW_PASSWORD, "ENCODED")).thenReturn(true);
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(member(MemberRole.USER, MemberStatus.ACTIVE)));
        stubTokenIssue();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.adminLogin(new LoginRequest(EMAIL, RAW_PASSWORD)));

        assertEquals(ResponseCode.FORBIDDEN_ACCESS, ex.getResponseCode());
    }

    @Test
    @DisplayName("refresh - 저장된 RefreshToken과 일치하면 토큰을 재발급한다")
    void refresh_Success() {
        String refreshToken = "stored-refresh-token";
        when(jwtTokenProvider.validate(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getClaims(refreshToken)).thenReturn(Map.of("email", EMAIL));
        when(refreshTokenService.find(EMAIL)).thenReturn(Optional.of(refreshToken));
        when(memberService.loadUserByUsername(EMAIL))
                .thenReturn(new MemberDetails(1L, EMAIL, "ENCODED", MemberRole.USER));
        stubTokenIssue();

        TokenResponse tokens = authService.refresh(refreshToken);

        assertEquals("access-token", tokens.accessToken());
        assertEquals("refresh-token", tokens.refreshToken());
    }

    @Test
    @DisplayName("refresh - 유효하지 않은 토큰이면 401(INVALID_REFRESH_TOKEN)")
    void refresh_InvalidToken() {
        when(jwtTokenProvider.validate("bad")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.refresh("bad"));

        assertEquals(ResponseCode.INVALID_REFRESH_TOKEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("refresh - 저장된 토큰과 불일치하면 401(REFRESH_TOKEN_MISMATCH)")
    void refresh_Mismatch() {
        String refreshToken = "client-refresh-token";
        when(jwtTokenProvider.validate(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getClaims(refreshToken)).thenReturn(Map.of("email", EMAIL));
        when(refreshTokenService.find(EMAIL)).thenReturn(Optional.of("server-different-token"));

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.refresh(refreshToken));

        assertEquals(ResponseCode.REFRESH_TOKEN_MISMATCH, ex.getResponseCode());
    }

    @Test
    @DisplayName("logout - RefreshToken(세션)을 삭제한다")
    void logout_Success() {
        authService.logout(EMAIL);

        verify(refreshTokenService).delete(EMAIL);
    }

    @Test
    @DisplayName("issueTokens - access/refresh 토큰을 만들고 RefreshToken을 저장한다")
    void issueTokens_Success() {
        stubTokenIssue();

        TokenResponse tokens = authService.issueTokens(EMAIL, MemberRole.USER);

        assertEquals("access-token", tokens.accessToken());
        assertEquals("refresh-token", tokens.refreshToken());
        verify(refreshTokenService).save(eq(EMAIL), eq("refresh-token"), anyString());
    }
}
