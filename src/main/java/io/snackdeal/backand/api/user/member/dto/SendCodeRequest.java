package io.snackdeal.backand.api.user.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증코드 발송 요청")
public record SendCodeRequest(
        // 인증코드를 받을 이메일
        @Schema(description = "인증받을 이메일", example = "test@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email
) {
}
