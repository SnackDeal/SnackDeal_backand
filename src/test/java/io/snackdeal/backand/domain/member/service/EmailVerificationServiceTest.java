package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.api.user.member.dto.SendCodeResponse;
import io.snackdeal.backand.api.user.member.dto.VerifyCodeResponse;
import io.snackdeal.backand.domain.member.entity.EmailVerification;
import io.snackdeal.backand.domain.member.repository.EmailVerificationRepository;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.global.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Mock
    private EmailVerificationRepository repository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MailService mailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailVerificationService, "codeExpirationMillis", 300_000L);
        ReflectionTestUtils.setField(emailVerificationService, "tokenExpirationMillis", 600_000L);
    }

    @Test
    @DisplayName("sendCode - 신규 이메일이면 인증코드를 발송하고 expiresIn(초)을 반환한다")
    void sendCode_Success() {
        String email = "new@test.com";
        when(memberRepository.existsByEmail(email)).thenReturn(false);
        when(repository.findTopByEmailOrderByIdDesc(email)).thenReturn(Optional.empty());

        SendCodeResponse response = emailVerificationService.sendCode(email);

        assertEquals(300, response.expiresIn());
        verify(repository).save(any(EmailVerification.class));
        verify(mailService).send(eq(email), anyString(), anyString());
    }

    @Test
    @DisplayName("sendCode - 이미 가입된 이메일이면 409(DUPLICATE_EMAIL)")
    void sendCode_DuplicateEmail() {
        String email = "exists@test.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> emailVerificationService.sendCode(email));

        assertEquals(ResponseCode.DUPLICATE_EMAIL, ex.getResponseCode());
        verify(repository, never()).save(any());
        verify(mailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("sendCode - 60초 이내 재요청이면 429(EMAIL_RESEND_TOO_SOON)")
    void sendCode_ResendTooSoon() {
        String email = "recent@test.com";
        when(memberRepository.existsByEmail(email)).thenReturn(false);
        // 방금 발송된 인증(createdAt = now)이 존재 → 쿨다운 미경과
        EmailVerification recent = EmailVerification.builder()
                .email(email)
                .code("123456")
                .codeExpiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        when(repository.findTopByEmailOrderByIdDesc(email)).thenReturn(Optional.of(recent));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> emailVerificationService.sendCode(email));

        assertEquals(ResponseCode.EMAIL_RESEND_TOO_SOON, ex.getResponseCode());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("verifyCode - 코드 일치 시 인증 토큰과 expiresIn을 반환한다")
    void verifyCode_Success() {
        String email = "test@test.com";
        String code = "482913";
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .codeExpiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        when(repository.findTopByEmailOrderByIdDesc(email)).thenReturn(Optional.of(verification));

        VerifyCodeResponse response = emailVerificationService.verifyCode(email, code);

        assertEquals(600, response.expiresIn());
        assertEquals(verification.getVerificationToken(), response.verificationToken());
    }

    @Test
    @DisplayName("verifyCode - 코드 불일치 시 400(EMAIL_CODE_MISMATCH)")
    void verifyCode_Mismatch() {
        String email = "test@test.com";
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code("482913")
                .codeExpiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        when(repository.findTopByEmailOrderByIdDesc(email)).thenReturn(Optional.of(verification));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> emailVerificationService.verifyCode(email, "000000"));

        assertEquals(ResponseCode.EMAIL_CODE_MISMATCH, ex.getResponseCode());
    }

    @Test
    @DisplayName("verifyCode - 코드 만료 시 400(EMAIL_CODE_EXPIRED)")
    void verifyCode_Expired() {
        String email = "test@test.com";
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code("482913")
                .codeExpiresAt(LocalDateTime.now().minusSeconds(1))
                .build();
        when(repository.findTopByEmailOrderByIdDesc(email)).thenReturn(Optional.of(verification));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> emailVerificationService.verifyCode(email, "482913"));

        assertEquals(ResponseCode.EMAIL_CODE_EXPIRED, ex.getResponseCode());
    }
}
