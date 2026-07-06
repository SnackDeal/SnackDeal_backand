package io.snackdeal.backand.api.user.member.controller;

import io.snackdeal.backand.api.user.member.dto.MemberDescription;
import io.snackdeal.backand.api.user.member.dto.MemberDetails;
import io.snackdeal.backand.api.user.member.dto.MemberUpdateRequest;
import io.snackdeal.backand.api.user.member.dto.SendCodeRequest;
import io.snackdeal.backand.api.user.member.dto.SendCodeResponse;
import io.snackdeal.backand.api.user.member.dto.VerifyCodeRequest;
import io.snackdeal.backand.api.user.member.dto.VerifyCodeResponse;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.service.AuthService;
import io.snackdeal.backand.domain.member.service.EmailVerificationService;
import io.snackdeal.backand.domain.member.service.MemberService;
import io.snackdeal.backand.global.config.dto.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 컨트롤러 단위테스트: Spring 컨텍스트 없이 서비스 위임/응답 래핑만 검증
 */
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;

    @Mock
    private MemberService memberService;
    @Mock
    private AuthService authService;
    @Mock
    private EmailVerificationService emailVerificationService;

    private MemberDetails details() {
        return new MemberDetails(1L, "user@test.com", "ENCODED", MemberRole.USER);
    }

    @Test
    @DisplayName("sendCode - EmailVerificationService 결과를 그대로 감싸 반환")
    void sendCode() {
        SendCodeResponse expected = new SendCodeResponse(300);
        when(emailVerificationService.sendCode("new@test.com")).thenReturn(expected);

        CommonResponse<SendCodeResponse> response =
                memberController.sendCode(new SendCodeRequest("new@test.com"));

        assertTrue(response.isSuccess());
        assertSame(expected, response.getData());
    }

    @Test
    @DisplayName("verifyCode - 인증 토큰 응답을 그대로 반환")
    void verifyCode() {
        VerifyCodeResponse expected = new VerifyCodeResponse("token-123", 600);
        when(emailVerificationService.verifyCode("a@test.com", "482913")).thenReturn(expected);

        CommonResponse<VerifyCodeResponse> response =
                memberController.verifyCode(new VerifyCodeRequest("a@test.com", "482913"));

        assertSame(expected, response.getData());
    }

    @Test
    @DisplayName("me - 인증된 사용자 이메일로 내 정보를 조회")
    void me() {
        MemberDescription expected = mockDescription();
        when(memberService.findDescriptionByEmail("user@test.com")).thenReturn(expected);

        CommonResponse<MemberDescription> response = memberController.me(details());

        assertSame(expected, response.getData());
        verify(memberService).findDescriptionByEmail("user@test.com");
    }

    @Test
    @DisplayName("updateMe - 인증된 사용자 이메일과 요청으로 프로필을 수정")
    void updateMe() {
        MemberUpdateRequest request = new MemberUpdateRequest("01099998888", "old", "newP@ss1!");
        MemberDescription expected = mockDescription();
        when(memberService.updateProfile("user@test.com", request)).thenReturn(expected);

        CommonResponse<MemberDescription> response = memberController.updateMe(details(), request);

        assertSame(expected, response.getData());
        verify(memberService).updateProfile("user@test.com", request);
    }

    @Test
    @DisplayName("logout - 인증된 사용자 이메일로 로그아웃을 위임")
    void logout() {
        CommonResponse<Void> response = memberController.logout(details());

        assertTrue(response.isSuccess());
        verify(authService).logout("user@test.com");
    }

    private MemberDescription mockDescription() {
        return new MemberDescription(1L, "user@test.com", "홍길동", "01011112222",
                null, null, null, MemberRole.USER, null, null);
    }
}
