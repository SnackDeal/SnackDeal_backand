package io.snackdeal.backand.api.user.member.controller;

import io.snackdeal.backand.global.config.dto.CommonResponse;
import io.snackdeal.backand.api.user.member.dto.*;
import io.snackdeal.backand.domain.member.service.AuthService;
import io.snackdeal.backand.domain.member.service.EmailVerificationService;
import io.snackdeal.backand.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/email/send-code")
    public CommonResponse<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        emailVerificationService.sendCode(request.email());
        return CommonResponse.success(null);
    }

    @PostMapping("/email/verify-code")
    public CommonResponse<VerifyCodeResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        return CommonResponse.success(emailVerificationService.verifyCode(request.email(), request.code()));
    }

    @PostMapping("/join")
    public CommonResponse<MemberDescription> join(@Valid @RequestBody JoinRequest request) {
        return CommonResponse.success(memberService.join(request));
    }

    @PostMapping("/login")
    public CommonResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return CommonResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    public CommonResponse<Void> logout(@AuthenticationPrincipal MemberDetails details) {
        authService.logout(details.getEmail());
        return CommonResponse.success(null);
    }

    @PostMapping("/token/refresh")
    public CommonResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return CommonResponse.success(authService.refresh(request.refreshToken()));
    }

    @GetMapping("/me")
    public CommonResponse<MemberDescription> me(@AuthenticationPrincipal MemberDetails details) {
        return CommonResponse.success(memberService.findDescriptionByEmail(details.getEmail()));
    }

    @PatchMapping("/me")
    public CommonResponse<MemberDescription> updateMe(@AuthenticationPrincipal MemberDetails details,
                                                        @Valid @RequestBody MemberUpdateRequest request) {
        return CommonResponse.success(memberService.updateProfile(details.getEmail(), request));
    }
}
