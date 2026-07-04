package io.snackdeal.backand.api.user.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 (일반/관리자 공용)")
public record LoginRequest(
        // Swagger "Try it out" 기본값은 시드된 사용자 계정. 관리자 로그인 시 admin@snackdeal.io 로 바꿔 사용.
        @Schema(description = "이메일", example = "user@snackdeal.io", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String email,

        @Schema(description = "비밀번호", example = "user1234", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String password
) {
}
