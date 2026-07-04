package io.snackdeal.backand.api.user.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급 요청")
public record RefreshRequest(
        // 로그인 시 발급받은 RefreshToken
        @Schema(description = "RefreshToken", example = "eyJhbGciOiJIUzI1NiJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String refreshToken
) {
}
