package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.api.user.member.dto.VerifyCodeResponse;
import io.snackdeal.backand.domain.member.entity.EmailVerification;
import io.snackdeal.backand.domain.member.repository.EmailVerificationRepository;
import io.snackdeal.backand.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailVerificationRepository repository;
    private final MailService mailService;

    @Value("${custom.email-verification.code-expiration}")
    private long codeExpirationMillis;

    @Value("${custom.email-verification.token-expiration}")
    private long tokenExpirationMillis;

    @Transactional
    public void sendCode(String email) {
        String code = String.valueOf(100000 + RANDOM.nextInt(900000));

        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .codeExpiresAt(LocalDateTime.now().plusSeconds(codeExpirationMillis / 1000))
                .build();

        repository.save(verification);

        mailService.send(email, "[SnackDeal] 이메일 인증코드", "인증코드: " + code + " (5분 이내 입력해주세요)");
    }

    @Transactional
    public VerifyCodeResponse verifyCode(String email, String code) {
        EmailVerification verification = repository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.EMAIL_CODE_MISMATCH));

        if (verification.getCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResponseCode.EMAIL_CODE_EXPIRED);
        }

        if (!verification.getCode().equals(code)) {
            throw new BusinessException(ResponseCode.EMAIL_CODE_MISMATCH);
        }

        String token = UUID.randomUUID().toString();
        verification.verify(token, LocalDateTime.now().plusSeconds(tokenExpirationMillis / 1000));

        return new VerifyCodeResponse(token);
    }

    public String getVerifiedEmail(String verificationToken) {
        EmailVerification verification = repository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new BusinessException(ResponseCode.EMAIL_TOKEN_INVALID));

        if (!verification.isVerified() || verification.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResponseCode.EMAIL_TOKEN_INVALID);
        }

        return verification.getEmail();
    }
}
