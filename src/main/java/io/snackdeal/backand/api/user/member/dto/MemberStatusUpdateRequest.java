package io.snackdeal.backand.api.user.member.dto;

import io.snackdeal.backand.domain.member.entity.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "회원 상태 변경 요청")
public record MemberStatusUpdateRequest(
        @Schema(description = "변경할 상태", example = "INACTIVE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull MemberStatus status,

        // 상태 변경 사유 (관리자 메모용, 선택)
        @Schema(description = "상태 변경 사유(관리자 메모용, 선택)", example = "6개월 미접속")
        String reason
) {
}
