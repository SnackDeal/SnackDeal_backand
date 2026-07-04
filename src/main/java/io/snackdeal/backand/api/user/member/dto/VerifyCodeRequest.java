package io.snackdeal.backand.api.user.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증코드 검증 요청")
public record VerifyCodeRequest(
        // 인증 대상 이메일
        @Schema(description = "인증 대상 이메일", example = "test@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email,

        // 메일로 받은 6자리 인증코드
        @Schema(description = "이메일로 받은 6자리 인증코드", example = "482913", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String code
) {
}
