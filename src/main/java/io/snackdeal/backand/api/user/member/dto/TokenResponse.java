package io.snackdeal.backand.api.user.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 응답 (로그인/재발급)")
public record TokenResponse(
        @Schema(description = "AccessToken (API 호출 시 Authorization: Bearer 로 사용)",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InVzZXJAc25hY2tkZWFsLmlvIn0.abc123")
        String accessToken,

        @Schema(description = "RefreshToken (AccessToken 만료 시 재발급용)",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InVzZXJAc25hY2tkZWFsLmlvIiwidHlwZSI6InJlZnJlc2gifQ.xyz789")
        String refreshToken
) {
}
