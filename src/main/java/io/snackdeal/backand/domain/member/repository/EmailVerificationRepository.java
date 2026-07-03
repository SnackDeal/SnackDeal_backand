package io.snackdeal.backand.domain.member.repository;

import io.snackdeal.backand.domain.member.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findTopByEmailOrderByIdDesc(String email);

    Optional<EmailVerification> findByVerificationToken(String verificationToken);
}
