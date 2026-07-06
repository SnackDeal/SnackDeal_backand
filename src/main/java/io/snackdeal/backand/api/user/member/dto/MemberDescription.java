package io.snackdeal.backand.api.user.member.dto;

import io.snackdeal.backand.domain.member.entity.Gender;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.entity.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원 정보 응답 내 정보 조회/수정, 관리자 회원 리스트/상세에서 공통으로 사용
 */
@Schema(description = "회원 정보")
public record MemberDescription(
        @Schema(description = "회원 id", example = "1")
        Long id,

        @Schema(description = "이메일", example = "test@test.com")
        String email,

        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "휴대폰번호", example = "01011112222")
        String phone,

        @Schema(description = "생년월일", example = "2000-01-01")
        LocalDate birth,

        @Schema(description = "성별", example = "MALE")
        Gender gender,

        @Schema(description = "상태(ACTIVE/INACTIVE/DELETED)", example = "ACTIVE")
        MemberStatus status,

        @Schema(description = "역할(USER/ADMIN)", example = "USER")
        MemberRole role,

        @Schema(description = "가입 일시", example = "2026-07-01T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "마지막 로그인 일시", example = "2026-07-01T14:32:00")
        LocalDateTime lastLogin
) {
}
