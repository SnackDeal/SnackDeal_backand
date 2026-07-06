package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.api.user.member.dto.SendCodeResponse;
import io.snackdeal.backand.api.user.member.dto.VerifyCodeResponse;
import io.snackdeal.backand.domain.member.entity.EmailVerification;
import io.snackdeal.backand.domain.member.repository.EmailVerificationRepository;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
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
    private static final long RESEND_COOLDOWN_SECONDS = 60;

    private final EmailVerificationRepository repository;
    private final MemberRepository memberRepository;
    private final MailService mailService;

    @Value("${custom.email-verification.code-expiration}")
    private long codeExpirationMillis;

    @Value("${custom.email-verification.token-expiration}")
    private long tokenExpirationMillis;

    /*
     * 이메일 인증코드 발송.
     * 회원가입 전 단계이므로, 아래 두 가지를 먼저 막고 발송
     *  1) 이미 가입된 이메일   → 409 CONFLICT (가입 완료된 계정에 인증코드를 또 보낼 이유가 없음)
     *  2) 60초 이내 재요청     → 429 TOO_MANY_REQUESTS (메일 폭탄/스팸 방지, 재발송 쿨다운)
     * 통과하면 6자리 코드를 생성해 저장하고 메일로 발송하며, 코드 유효시간(초)을 응답으로 돌려준다.
     */
    @Transactional
    public SendCodeResponse sendCode(String email) {
        // 1) 이미 가입된 이메일이면 발송 자체를 막는다
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ResponseCode.DUPLICATE_EMAIL);
        }

        // 2) 가장 최근 발송 이력을 확인해 60초(RESEND_COOLDOWN_SECONDS) 쿨다운이 지났는지 검사
        repository.findTopByEmailOrderByIdDesc(email).ifPresent(latest -> {
            LocalDateTime resendableAt = latest.getCreatedAt().plusSeconds(RESEND_COOLDOWN_SECONDS);
            if (resendableAt.isAfter(LocalDateTime.now())) {
                throw new BusinessException(ResponseCode.EMAIL_RESEND_TOO_SOON);
            }
        });

        // 6자리 숫자 코드 (100000~999999) expiresIn 은 ms 설정값을 초 단위로 변환해 응답에 노출
        String code = String.valueOf(100000 + RANDOM.nextInt(900000));
        int expiresIn = (int) (codeExpirationMillis / 1000);

        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .codeExpiresAt(LocalDateTime.now().plusSeconds(expiresIn))
                .build();

        repository.save(verification);

        mailService.send(email, "[SnackDeal] 이메일 인증코드", "인증코드: " + code + " (5분 이내 입력해주세요)");

        return new SendCodeResponse(expiresIn);
    }

    /*
     * 이메일 인증코드 검증.
     * 가장 최근 발송된 코드와 대조하여, 만료/불일치면 예외를 던진다.
     * 성공하면 회원가입 API 에서 사용할 인증 토큰(verification_token)을 발급하고 유효시간(초)을 함께 반환
     * → 이후 join 시 이 토큰으로 "이 이메일은 인증됐다"는 것을 증명 (getVerifiedEmail 참고)
     */
    @Transactional
    public VerifyCodeResponse verifyCode(String email, String code) {
        // 해당 이메일의 최신 발송 이력이 없으면 대조할 코드 자체가 없음 → 불일치로 처리
        EmailVerification verification = repository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.EMAIL_CODE_MISMATCH));

        // 유효시간(5분) 경과 검사
        if (verification.getCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResponseCode.EMAIL_CODE_EXPIRED);
        }

        // 코드 값 일치 검사
        if (!verification.getCode().equals(code)) {
            throw new BusinessException(ResponseCode.EMAIL_CODE_MISMATCH);
        }

        // 검증 성공 → 회원가입에서 쓸 인증 토큰 발급(UUID) + 토큰 만료시각 기록
        String token = UUID.randomUUID().toString();
        int expiresIn = (int) (tokenExpirationMillis / 1000);
        verification.verify(token, LocalDateTime.now().plusSeconds(expiresIn));

        return new VerifyCodeResponse(token, expiresIn);
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
