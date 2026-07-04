package io.snackdeal.backand.domain.member.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 이메일 인증 이력 엔티티.
 * 회원가입 전, "이메일 발송 → 코드 검증 → 인증 토큰 발급"의 상태를 한 행에 기록한다.
 * 한 이메일에 대해 재발송할 때마다 새 행이 쌓이며, 최신 행(id 최대)을 기준으로 검증한다.
 */
@Schema(description = "이메일 인증 이력 엔티티")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {

    @Schema(description = "인증 이력 id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "인증 대상 이메일", example = "test@test.com")
    private String email;         // 인증 대상 이메일

    @Schema(description = "발송한 6자리 인증코드", example = "482913")
    private String code;          // 메일로 발송한 6자리 인증코드

    @Schema(description = "검증 성공 시 발급하는 인증 토큰")
    private String verificationToken; // 코드 검증 성공 시 발급하는 인증 토큰 (회원가입에서 사용)

    @Schema(description = "인증코드 만료 시각(발송 후 5분)")
    private LocalDateTime codeExpiresAt;  // 인증코드 만료 시각 (발송 후 5분)

    @Schema(description = "인증 토큰 만료 시각(검증 후 10분)")
    private LocalDateTime tokenExpiresAt; // 인증 토큰 만료 시각 (검증 후 10분)

    @Schema(description = "코드 검증 완료 여부")
    private boolean verified;     // 코드 검증 완료 여부

    @Schema(description = "발송 시각(60초 재발송 쿨다운 기준)")
    private LocalDateTime createdAt; // 발송 시각 (60초 재발송 쿨다운 판정 기준)

    @Schema(description = "가입 완료 후 연결되는 회원 id")
    private Long memberId;        // 가입 완료 후 연결되는 회원 id (선택)

    @Builder
    public EmailVerification(String email, String code, LocalDateTime codeExpiresAt) {
        this.email = email;
        this.code = code;
        this.codeExpiresAt = codeExpiresAt;
        this.verified = false;
        this.createdAt = LocalDateTime.now();
    }

    // 코드 검증 성공 처리: 인증 토큰과 그 만료시각을 기록하고 verified=true 로 전환
    public void verify(String verificationToken, LocalDateTime tokenExpiresAt) {
        this.verified = true;
        this.verificationToken = verificationToken;
        this.tokenExpiresAt = tokenExpiresAt;
    }
}
