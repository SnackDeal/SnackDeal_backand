package io.snackdeal.backand.api.user.member.controller;

import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.global.swagger.MemberApiDocs;
import io.snackdeal.backand.api.user.member.dto.*;
import io.snackdeal.backand.domain.member.service.AuthService;
import io.snackdeal.backand.domain.member.service.EmailVerificationService;
import io.snackdeal.backand.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 회원 API 이메일 인증 → 회원가입 → 로그인/로그아웃/토큰재발급 → 내 정보 조회/수정을 담당
 * 인증이 필요한 엔드포인트(me/updateMe/logout)는 @AuthenticationPrincipal 로 로그인 사용자 정보를 받는다.
 * Swagger 설명은 global 의 합성 어노테이션(@MemberApiDocs.*)에서 가져온다.
 */
@MemberApiDocs.Doc
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    // 이메일 인증코드 발송
    @MemberApiDocs.SendCode
    @PostMapping("/email/send-code")
    public CommonResponse<SendCodeResponse> sendCode(@Valid @RequestBody SendCodeRequest request) {
        return CommonResponse.success(emailVerificationService.sendCode(request.email()));
    }

    // 이메일 인증코드 검증 → 회원가입에 쓸 인증 토큰 발급
    @MemberApiDocs.VerifyCode
    @PostMapping("/email/verify-code")
    public CommonResponse<VerifyCodeResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        return CommonResponse.success(emailVerificationService.verifyCode(request.email(), request.code()));
    }

    // 회원가입 (소셜 회원가입인 경우 가입과 동시에 로그인 토큰을 발급)
    @MemberApiDocs.Join
    @PostMapping("/join")
    public CommonResponse<JoinResponse> join(@Valid @RequestBody JoinRequest request) {
        MemberDescription member = memberService.join(request);
        if (request.socialLogin()) {
            TokenResponse tokens = authService.issueTokens(member.email(), member.role());
            return CommonResponse.success(JoinResponse.of(member, tokens));
        }
        return CommonResponse.success(JoinResponse.of(member));
    }

    // 로그인
    @MemberApiDocs.Login
    @PostMapping("/login")
    public CommonResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return CommonResponse.success(authService.login(request));
    }

    // 로그아웃
    @MemberApiDocs.Logout
    @PostMapping("/logout")
    public CommonResponse<Void> logout(@AuthenticationPrincipal MemberDetails details) {
        authService.logout(details.getEmail());
        return CommonResponse.success(null);
    }

    // 토큰 재발급
    @MemberApiDocs.TokenRefresh
    @PostMapping("/token/refresh")
    public CommonResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return CommonResponse.success(authService.refresh(request.refreshToken()));
    }

    // 내 정보 조회 (로그인 사용자 본인)
    @MemberApiDocs.GetMe
    @GetMapping("/me")
    public CommonResponse<MemberDescription> me(@AuthenticationPrincipal MemberDetails details) {
        return CommonResponse.success(memberService.findDescriptionByEmail(details.getEmail()));
    }

    // 내 정보 수정 (휴대폰/비밀번호) 비밀번호 변경 시 현재 비밀번호 검증은 서비스에서 수행.
    @MemberApiDocs.UpdateMe
    @PatchMapping("/me")
    public CommonResponse<MemberDescription> updateMe(@AuthenticationPrincipal MemberDetails details,
                                                        @Valid @RequestBody MemberUpdateRequest request) {
        return CommonResponse.success(memberService.updateProfile(details.getEmail(), request));
    }
}
