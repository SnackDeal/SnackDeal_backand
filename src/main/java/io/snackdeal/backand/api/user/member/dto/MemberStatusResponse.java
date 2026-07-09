package io.snackdeal.backand.api.user.member.dto;

import io.snackdeal.backand.domain.member.entity.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 관리자 회원 상태 변경 응답.
 */
@Schema(description = "회원 상태 변경 응답")
public record MemberStatusResponse(
        @Schema(description = "회원 id", example = "45")
        Long id,

        @Schema(description = "이메일", example = "hong@test.com")
        String email,

        @Schema(description = "변경된 상태", example = "INACTIVE")
        MemberStatus status,

        @Schema(description = "변경 일시", example = "2026-07-02T10:00:00")
        LocalDateTime updatedAt
) {
}
