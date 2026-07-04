package io.snackdeal.backand.api.user.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 인증코드 검증 응답. expiresIn: 발급된 인증 토큰의 유효시간(초).
 */
@Schema(description = "이메일 인증코드 검증 응답")
public record VerifyCodeResponse(
        @Schema(description = "회원가입에 사용할 인증 토큰", example = "evt_a1b2c3d4")
        String verificationToken,

        @Schema(description = "인증 토큰 유효시간(초)", example = "600")
        int expiresIn
) {
}
