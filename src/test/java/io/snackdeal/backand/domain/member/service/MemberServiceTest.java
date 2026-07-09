package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.api.user.member.dto.JoinRequest;
import io.snackdeal.backand.api.user.member.dto.MemberStatusResponse;
import io.snackdeal.backand.api.user.member.dto.MemberStatusUpdateRequest;
import io.snackdeal.backand.api.user.member.dto.MemberUpdateRequest;
import io.snackdeal.backand.domain.member.entity.Gender;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.entity.MemberStatus;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.domain.coupon.service.CouponService;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository repository;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CouponService couponService;

    private Member member(Long id, MemberStatus status) {
        Member m = Member.builder()
                .email("hong@test.com")
                .password("ENCODED_OLD")
                .name("홍길동")
                .birth(LocalDate.of(2000, 1, 1))
                .gender(Gender.MALE)
                .phone("01011112222")
                .role(MemberRole.USER)
                .build();
        ReflectionTestUtils.setField(m, "id", id);
        if (status != MemberStatus.ACTIVE) {
            m.changeStatus(status);
        }
        return m;
    }

    @Test
    @DisplayName("updateProfile - 비밀번호 변경 시 현재 비밀번호 불일치면 401(INVALID_PASSWORD)")
    void updateProfile_WrongCurrentPassword() {
        Member m = member(1L, MemberStatus.ACTIVE);
        when(repository.findByEmail(m.getEmail())).thenReturn(Optional.of(m));
        when(passwordEncoder.matches("wrong", "ENCODED_OLD")).thenReturn(false);

        MemberUpdateRequest request = new MemberUpdateRequest(null, "wrong", "newP@ss1!");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.updateProfile(m.getEmail(), request));

        assertEquals(ResponseCode.INVALID_PASSWORD, ex.getResponseCode());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("updateProfile - 현재 비밀번호가 맞으면 새 비밀번호/휴대폰으로 수정된다")
    void updateProfile_Success() {
        Member m = member(1L, MemberStatus.ACTIVE);
        when(repository.findByEmail(m.getEmail())).thenReturn(Optional.of(m));
        when(passwordEncoder.matches("old", "ENCODED_OLD")).thenReturn(true);
        when(passwordEncoder.encode("newP@ss1!")).thenReturn("ENCODED_NEW");

        MemberUpdateRequest request = new MemberUpdateRequest("010-9999-8888", "old", "newP@ss1!");

        var result = memberService.updateProfile(m.getEmail(), request);

        assertEquals("010-9999-8888", result.phone());
        assertEquals("ENCODED_NEW", m.getPassword());
    }

    @Test
    @DisplayName("changeStatus - 본인 계정이면 403(SELF_STATUS_CHANGE_FORBIDDEN)")
    void changeStatus_SelfForbidden() {
        Member m = member(1L, MemberStatus.ACTIVE);
        when(repository.findById(1L)).thenReturn(Optional.of(m));

        MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(MemberStatus.INACTIVE, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.changeStatus(1L, request, 1L));

        assertEquals(ResponseCode.SELF_STATUS_CHANGE_FORBIDDEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("changeStatus - 이미 탈퇴한 회원이면 422(INVALID_MEMBER_STATUS_TRANSITION)")
    void changeStatus_DeletedIsTerminal() {
        Member m = member(2L, MemberStatus.DELETED);
        when(repository.findById(2L)).thenReturn(Optional.of(m));

        MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(MemberStatus.ACTIVE, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.changeStatus(2L, request, 99L));

        assertEquals(ResponseCode.INVALID_MEMBER_STATUS_TRANSITION, ex.getResponseCode());
    }

    @Test
    @DisplayName("changeStatus - DELETED로 변경하면 상태 변경 + 토큰(세션) 무효화")
    void changeStatus_DeleteInvalidatesToken() {
        Member m = member(2L, MemberStatus.ACTIVE);
        when(repository.findById(2L)).thenReturn(Optional.of(m));

        MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(MemberStatus.DELETED, "규정 위반");

        MemberStatusResponse response = memberService.changeStatus(2L, request, 99L);

        assertEquals(MemberStatus.DELETED, response.status());
        verify(refreshTokenService).delete(m.getEmail());
    }

    @Test
    @DisplayName("changeStatus - INACTIVE 변경은 토큰을 무효화하지 않음")
    void changeStatus_InactiveKeepsToken() {
        Member m = member(2L, MemberStatus.ACTIVE);
        when(repository.findById(2L)).thenReturn(Optional.of(m));

        MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(MemberStatus.INACTIVE, "6개월 미접속");

        MemberStatusResponse response = memberService.changeStatus(2L, request, 99L);

        assertEquals(MemberStatus.INACTIVE, response.status());
        verify(refreshTokenService, never()).delete(anyString());
    }

    @Test
    @DisplayName("findById - 존재하지 않는 회원이면 404(MEMBER_NOT_FOUND)")
    void findById_NotFound() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.findById(404L));

        assertEquals(ResponseCode.MEMBER_NOT_FOUND, ex.getResponseCode());
    }

    private JoinRequest joinRequest(String email) {
        return new JoinRequest(email, "newP@ss1!", "홍길동",
                LocalDate.of(2000, 1, 1), Gender.MALE, "01011112222", "vtoken", false);
    }

    @Test
    @DisplayName("join - 인증토큰이 유효하고 미가입 이메일이면 회원을 생성")
    void join_Success() {
        String email = "hong@test.com";
        when(emailVerificationService.getVerifiedEmail("vtoken")).thenReturn(email);
        when(repository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newP@ss1!")).thenReturn("ENCODED");
        when(repository.saveAndFlush(any(Member.class))).thenReturn(member(1L, MemberStatus.ACTIVE));

        var result = memberService.join(joinRequest(email));

        assertEquals(email, result.email());
        verify(repository).saveAndFlush(any(Member.class));
        verify(couponService).issueSigninCoupons(1L);
    }

    @Test
    @DisplayName("join - 인증토큰의 이메일과 요청 이메일이 다르면 EMAIL_TOKEN_INVALID")
    void join_TokenEmailMismatch() {
        when(emailVerificationService.getVerifiedEmail("vtoken")).thenReturn("other@test.com");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.join(joinRequest("hong@test.com")));

        assertEquals(ResponseCode.EMAIL_TOKEN_INVALID, ex.getResponseCode());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("join - 이미 가입된 이메일이면 DUPLICATE_EMAIL")
    void join_DuplicateEmail() {
        String email = "hong@test.com";
        when(emailVerificationService.getVerifiedEmail("vtoken")).thenReturn(email);
        when(repository.findByEmail(email)).thenReturn(Optional.of(member(1L, MemberStatus.ACTIVE)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> memberService.join(joinRequest(email)));

        assertEquals(ResponseCode.DUPLICATE_EMAIL, ex.getResponseCode());
        verify(repository, never()).saveAndFlush(any());
    }
}
