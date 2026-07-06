package io.snackdeal.backand.domain.member.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원 엔티티.
 * 탈퇴는 실제 삭제(hard delete)하지 않고 status=DELETED + deletedAt 기록으로 처리하여 주문/문의 이력을 보존
 * status/gender/role 은 문자열로 저장(@Enumerated STRING)하여 DB 값의 가독성을 높인다.
 */
@Schema(description = "회원 엔티티")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Schema(description = "회원 id", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "이메일(로그인 아이디, 유니크)", example = "test@test.com")
    private String email;         // 로그인 아이디로 사용 (유니크)

    @Schema(description = "암호화된 비밀번호(응답에는 노출하지 않음)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;      // 암호화되어 저장 (평문 저장 금지)

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "상태", example = "ACTIVE")
    @Enumerated(EnumType.STRING)
    private MemberStatus status;  // ACTIVE(정상) / INACTIVE(휴면) / DELETED(탈퇴)

    @Schema(description = "탈퇴 시각(DELETED 전환 시 기록)")
    private LocalDateTime deletedAt; // 탈퇴 시각 (DELETED 로 전환될 때만 기록)

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;

    @Schema(description = "가입 일시")
    private LocalDateTime createdAt;

    @Schema(description = "생년월일", example = "2000-01-01")
    private LocalDate birth;

    @Schema(description = "성별", example = "MALE")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Schema(description = "마지막 로그인 시각")
    private LocalDateTime lastLogin; // 마지막 로그인 시각 (로그인 성공 시 갱신)

    @Schema(description = "역할", example = "USER")
    @Enumerated(EnumType.STRING)
    private MemberRole role;      // USER / ADMIN

    @Schema(description = "휴대폰번호", example = "01011112222")
    private String phone;

    @Builder
    public Member(String email, String password, String name, LocalDate birth, Gender gender, String phone, MemberRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = MemberStatus.ACTIVE;
        this.birth = birth;
        this.gender = gender;
        this.phone = phone;
        this.role = (role != null) ? role : MemberRole.USER;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 프로필 부분 수정: null 인 항목은 건너뛰어 기존 값을 유지 (phone/password 각각 선택 변경)
    public void updateProfile(String phone, String password) {
        if (phone != null) {
            this.phone = phone;
        }
        if (password != null) {
            this.password = password;
        }
        this.updatedAt = LocalDateTime.now();
    }

    // 로그인 성공 시 마지막 로그인 시각 갱신
    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    // 상태 변경 DELETED 로 바뀔 때는 탈퇴 시각(deletedAt)도 함께 남긴다.
    public void changeStatus(MemberStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        if (status == MemberStatus.DELETED) {
            this.deletedAt = LocalDateTime.now();
        }
    }
}
