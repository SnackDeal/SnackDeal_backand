package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.api.user.member.dto.LoginRequest;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.member.dto.TokenResponse;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.entity.MemberStatus;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Value("${custom.jwt.expiration}")
    private long accessExpiration;

    @Value("${custom.jwt.refresh-expiration}")
    private long refreshExpiration;

    /*
     * 일반 로그인.
     * 1) 이메일로 사용자 조회 → 없으면 401(USER_NOT_FOUND)
     * 2) 비밀번호 대조 → 틀리면 401(INVALID_PASSWORD)
     * 3) 탈퇴 계정이면 차단 → 401(ACCOUNT_DELETED)
     * 4) 마지막 로그인 시각 기록 후 access/refresh 토큰 발급
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        MemberDetails details;
        try {
            details = (MemberDetails) memberService.loadUserByUsername(request.email());
        } catch (UsernameNotFoundException e) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(request.password(), details.getPassword())) {
            throw new BusinessException(ResponseCode.INVALID_PASSWORD);
        }

        Member member = memberRepository.findByEmail(request.email()).orElseThrow();

        // 탈퇴(DELETED)한 계정은 비밀번호가 맞아도 로그인 차단 (관리자가 상태를 DELETED 로 바꾸면 재로그인 불가)
        if (member.getStatus() == MemberStatus.DELETED) {
            throw new BusinessException(ResponseCode.ACCOUNT_DELETED);
        }

        // 로그인 성공 시점의 마지막 로그인 시각 기록 (대시보드/회원 상세에서 활용)
        member.recordLogin();

        return issueTokens(details.getEmail(), details.getRole());
    }

    /*
     * 관리자 로그인.
     * 일반 로그인 절차를 그대로 수행한 뒤, 역할이 ADMIN 이 아니면 403(FORBIDDEN_ACCESS) 로 막는다.
     */
    @Transactional
    public TokenResponse adminLogin(LoginRequest request) {
        TokenResponse tokens = login(request);
        Member member = memberRepository.findByEmail(request.email()).orElseThrow();
        if (member.getRole() != MemberRole.ADMIN) {
            throw new BusinessException(ResponseCode.FORBIDDEN_ACCESS);
        }
        return tokens;
    }

    /*
     * 토큰 재발급.
     * 1) RefreshToken 서명/만료 검증 → 실패 시 401(INVALID_REFRESH_TOKEN)
     * 2) Redis 에 저장된 토큰과 비교
     *    - 없으면(만료/로그아웃) 401(REFRESH_TOKEN_EXPIRED)
     *    - 값이 다르면(재사용/탈취 의심) 401(REFRESH_TOKEN_MISMATCH)
     * 3) 통과하면 새 access/refresh 토큰 발급 (토큰 로테이션)
     */
    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validate(refreshToken)) {
            throw new BusinessException(ResponseCode.INVALID_REFRESH_TOKEN);
        }

        Map<String, Object> claims = jwtTokenProvider.getClaims(refreshToken);
        String email = claims.get("email").toString();

        String stored = refreshTokenService.find(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.REFRESH_TOKEN_EXPIRED));

        if (!stored.equals(refreshToken)) {
            throw new BusinessException(ResponseCode.REFRESH_TOKEN_MISMATCH);
        }

        MemberDetails details = (MemberDetails) memberService.loadUserByUsername(email);
        return issueTokens(email, details.getRole());
    }

    // 로그아웃: Redis 의 RefreshToken/세션을 삭제해 이후 재발급을 막는다 (AccessToken 은 짧은 만료로 자연 소멸)
    public void logout(String email) {
        refreshTokenService.delete(email);
    }

    /*
     * access/refresh 토큰을 함께 발급하고 RefreshToken 을 Redis 에 저장
     * 매 발급마다 새 세션ID(sid)를 부여해 로그인 세션을 구분
     */
    public TokenResponse issueTokens(String email, MemberRole role) {
        String sessionId = UUID.randomUUID().toString();
        String accessToken = jwtTokenProvider.issue(accessExpiration, Map.of("email", email, "role", role.name(), "sid", sessionId));
        String refreshToken = jwtTokenProvider.issueRefreshToken(refreshExpiration, email, sessionId);
        refreshTokenService.save(email, refreshToken, sessionId);
        return new TokenResponse(accessToken, refreshToken);
    }
}
