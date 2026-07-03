package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.api.user.member.dto.LoginRequest;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.member.dto.TokenResponse;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
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
        member.recordLogin();

        return issueTokens(details.getEmail(), details.getRole());
    }

    @Transactional
    public TokenResponse adminLogin(LoginRequest request) {
        TokenResponse tokens = login(request);
        Member member = memberRepository.findByEmail(request.email()).orElseThrow();
        if (member.getRole() != MemberRole.ADMIN) {
            throw new BusinessException(ResponseCode.FORBIDDEN_ACCESS);
        }
        return tokens;
    }

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

    public void logout(String email) {
        refreshTokenService.delete(email);
    }

    public TokenResponse issueTokens(String email, MemberRole role) {
        String sessionId = UUID.randomUUID().toString();
        String accessToken = jwtTokenProvider.issue(accessExpiration, Map.of("email", email, "role", role.name(), "sid", sessionId));
        String refreshToken = jwtTokenProvider.issueRefreshToken(refreshExpiration, email, sessionId);
        refreshTokenService.save(email, refreshToken, sessionId);
        return new TokenResponse(accessToken, refreshToken);
    }
}
