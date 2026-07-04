package io.snackdeal.backand.api.user.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "내 정보 수정 요청 (모든 필드 선택. 비밀번호 변경 시 currentPassword 필수)")
public record MemberUpdateRequest(
        @Schema(description = "새 휴대폰번호(변경 시)", example = "01099998888")
        String phone,

        // 비밀번호 변경 시 현재 비밀번호(검증용)
        @Schema(description = "현재 비밀번호(비밀번호 변경 시 필수)", example = "oldP@ss1!")
        String currentPassword,

        // 새 비밀번호 (변경 시에만)
        @Schema(description = "새 비밀번호(변경 시)", example = "newP@ss1!")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8자 이상 16자 이하여야 합니다."
        ) String password
) {
}
