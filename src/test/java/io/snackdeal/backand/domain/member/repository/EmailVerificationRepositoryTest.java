package io.snackdeal.backand.domain.member.repository;

import io.snackdeal.backand.domain.member.entity.EmailVerification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class EmailVerificationRepositoryTest {

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    private EmailVerification save(String email, String code) {
        EmailVerification v = EmailVerification.builder()
                .email(email)
                .code(code)
                .codeExpiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        return emailVerificationRepository.save(v);
    }

    @Test
    @DisplayName("findTopByEmailOrderByIdDesc - 같은 이메일이면 가장 최근(id 큰) 인증을 반환")
    void findTopByEmailOrderByIdDesc() {
        String email = "hong@test.com";
        save(email, "111111"); // 먼저 발송(오래된 것)
        save(email, "222222"); // 나중 발송(최신)

        Optional<EmailVerification> latest = emailVerificationRepository.findTopByEmailOrderByIdDesc(email);

        assertTrue(latest.isPresent());
        assertEquals("222222", latest.get().getCode());
    }

    @Test
    @DisplayName("findByVerificationToken - 검증 완료로 발급된 토큰으로 조회된다")
    void findByVerificationToken() {
        EmailVerification v = save("hong@test.com", "482913");
        v.verify("evt_token_123", LocalDateTime.now().plusMinutes(10)); // 검증 성공 → 토큰 발급
        emailVerificationRepository.save(v);

        Optional<EmailVerification> found = emailVerificationRepository.findByVerificationToken("evt_token_123");

        assertTrue(found.isPresent());
        assertEquals("hong@test.com", found.get().getEmail());
        assertTrue(found.get().isVerified());
    }

    @Test
    @DisplayName("findByVerificationToken - 존재하지 않는 토큰이면 빈 Optional")
    void findByVerificationToken_NotFound() {
        Optional<EmailVerification> found = emailVerificationRepository.findByVerificationToken("no-such-token");

        assertFalse(found.isPresent());
    }
}
