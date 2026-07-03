package io.snackdeal.backand.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String code;

    private String verificationToken;

    private LocalDateTime codeExpiresAt;
    private LocalDateTime tokenExpiresAt;

    private boolean verified;

    private LocalDateTime createdAt;

    private Long memberId;

    @Builder
    public EmailVerification(String email, String code, LocalDateTime codeExpiresAt) {
        this.email = email;
        this.code = code;
        this.codeExpiresAt = codeExpiresAt;
        this.verified = false;
        this.createdAt = LocalDateTime.now();
    }

    public void verify(String verificationToken, LocalDateTime tokenExpiresAt) {
        this.verified = true;
        this.verificationToken = verificationToken;
        this.tokenExpiresAt = tokenExpiresAt;
    }
}
